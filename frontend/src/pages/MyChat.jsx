import { useEffect, useState } from 'react';
import NavigationBar from '../components/NavigationBar';
import { getCookie } from '../utils/Cookie';
import MyChatInfo from '../components/MyChatInfo';
import CreateChatButton from '../components/CreateChatButton';

// const mockUserChats = [
//     { id: 1, name: "Friday Night Pizza", lastMessage: "Who's in for pizza tonight?", timestamp: "2 min ago" },
//     { id: 2, name: "Sushi Lovers", lastMessage: "I found a great new sushi place!", timestamp: "1 hour ago" },
//     { id: 3, name: "Taco Tuesday", lastMessage: "Don't forget, it's Taco Tuesday tomorrow!", timestamp: "Yesterday" },
// ];


export default function MyChat() {

    const SERVER_URL = 'http://localhost:8080';
    const CREATED_URI = '/chat/list/created';

    const [myChats, setMyChats] = useState([]);
    const [selectedChat, setSelectedChat] = useState(null);

    useEffect(() => {
        fetch(SERVER_URL + CREATED_URI,
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
                    myChats.push(element);
                });
                setMyChats([...myChats]);
            });
    }, []);

    useEffect(() => {
        if (selectedChat) {
            window.location.href = `/chat?userChatId=${selectedChat.userChatId}`;
        }
    }, [selectedChat]);

    return (
        <div className="flex flex-col h-screen bg-gray-100">
            {/* Header */}
            <header className="bg-white shadow-md p-4 sticky top-0 z-10">
                <h1 className="text-2xl font-bold text-gray-800">My Chats</h1>
            </header>

            {/* Main Content Area */}
            <main className="flex-grow overflow-y-auto p-4">
                {myChats.length > 0 ? (
                    <ul className="space-y-4">
                        {myChats.map((chat) => (
                            <MyChatInfo  {...chat} setSelectedChat={setSelectedChat} />
                        ))}
                    </ul>
                ) : (
                    <div className="text-center py-8">
                        <p className="text-gray-600">You haven't created any chat rooms yet.</p>
                        {/* <button
                            className="mt-4 bg-yellow-500 text-white px-4 py-2 rounded-md hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-opacity-50"
                            onClick={() => console.log('Navigate to main page')}
                        >
                            Find Chat Rooms
                        </button> */}
                    </div>
                )}
            </main>

            {/* Bottom Navigation Bar */}
            <NavigationBar activeTab='chat'></NavigationBar>

            {/* Create Chat Button */}
            <CreateChatButton />
        </div>
    );
}