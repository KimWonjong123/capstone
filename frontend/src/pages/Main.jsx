import React, { useEffect, useState } from 'react';
import { Search } from 'lucide-react';
import NavigationBar from '../components/NavigationBar';
import { getCookie } from '../utils/Cookie';
import CreateChatButton from '../components/CreateChatButton';
import ChatInfo from '../components/ChatInfo';

export default function MainPage() {
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedChat, setSelectedChat] = useState(null);

    const handleSearch = (e) => {
        e.preventDefault();
        window.location.href = `/search?name=${searchQuery}`;
    };
    const [joiningChats, setJoiningChats] = useState([]);

    const SERVER_URL = "http://localhost:8080";
    const CHAT_URI = "/chat/list/joining";

    useEffect(() => {
        fetch(SERVER_URL + CHAT_URI,
            {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + getCookie('accessToken'),
                }
            }
        )
            .catch((error) => console.error('Error:', error))
            .then((response) => response.json())
            .then((data) => {
                data.forEach(element => {
                    joiningChats.push(element);
                });
                setJoiningChats([...joiningChats]);
                console.log(joiningChats)
            });
    }, []);

    useEffect(() => {
        if (selectedChat) {
            window.location.href = `/chat?userChatId=${selectedChat.userChatId}`;
        }
    }, [selectedChat])

    return (
        <div className="flex flex-col h-screen bg-gray-100">
            {/* Top Search Bar */}
            <header className="bg-white shadow-md p-4 sticky top-0 z-10">
                <form onSubmit={handleSearch} className="flex items-center">
                    <input
                        type="text"
                        placeholder="Search for chat rooms..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="flex-grow p-2 border border-gray-300 rounded-l-md focus:outline-none focus:ring-2 focus:ring-yellow-400"
                        aria-label="Search for chat rooms"
                    />
                    <button
                        type="submit"
                        className="bg-yellow-400 p-2 rounded-r-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-yellow-600"
                        aria-label="Submit search"
                    >
                        <Search className="text-white" />
                    </button>
                </form>
            </header>

            {/* Main Content Area */}
            <main className="flex-grow overflow-y-auto p-4">
                <h1 className="text-2xl font-bold mb-4">Joining Chat List</h1>

                {/* Placeholder for chat room list or other content */}
                <div className="space-y-4">
                    {/* {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((item) => (
                        <div key={item} className="bg-white p-4 rounded-md shadow">
                            <h2 className="font-semibold">Chat Room {item}</h2>
                            <p className="text-sm text-gray-600">Join this room to order food together!</p>
                        </div>
                    ))} */}
                    {joiningChats.length > 0 ? (
                        <ul className="space-y-4">
                            {joiningChats.map((chat) => (
                                <ChatInfo {...chat} setSelectedChat={setSelectedChat} />
                            ))}
                        </ul>
                    ) : (
                        <div className="text-center py-8">
                                <p className="text-gray-600">
                                    You haven't joined any chat rooms yet.
                                    Find a chat room to order food together!
                            </p>
                        </div>
                    )}
                </div>
            </main>

            {/* Bottom Navigation Bar */}
            <NavigationBar activeTab="home" />

            {/* Add Chat Room Button */}
            <CreateChatButton />

        </div>
    );
}