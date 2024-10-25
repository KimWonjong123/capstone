import { useEffect, useState } from 'react';
import { Search, X } from 'lucide-react';
import NavigationBar from '../components/NavigationBar';
import { getCookie } from '../utils/Cookie';
import ChatInfo from '../components/ChatInfo';

// Mock search results (replace with actual search logic in a real application)
const mockSearchResults = [
    { id: 1, name: "Pizza Lovers", lastActive: "2 min ago" },
    { id: 2, name: "Sushi Night", lastActive: "1 hour ago" },
    { id: 3, name: "Taco Tuesday Group", lastActive: "30 min ago" },
];

export default function SearchResults() {

    const SERVER_URL = 'http://localhost:8080';
    const SEARCH_URI = '/chat/search';
    const JOIN_URI = '/chat/join';
    
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedChat, setSelectedChat] = useState(null);

    const keyword = new URLSearchParams(window.location.search).get('name');

    if (keyword === null) {
        window.location.href = '/main';
    }

    const [searchResults, setSearchResults] = useState([]);

    useEffect(() => {
        fetch(SERVER_URL + SEARCH_URI + "?name=" + keyword,
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
                console.log(data);
                data.forEach(element => {
                    element.userName = element.ownerName;
                    searchResults.push(element);
                });
                setSearchResults([...searchResults]);
            });
    }, []);

    useEffect(() => {
        setSearchQuery(keyword);
    }, [keyword]);

    const handleSearch = (e) => {
        e.preventDefault();
        window.location.href = `/search?name=${searchQuery}`;
    };

    const handleJoinChat = () => {
        // Implement join chat logic here
        console.log('Joining chat:', selectedChat);
        fetch(SERVER_URL + JOIN_URI + "?chatId=" + selectedChat.chatId,
            {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + getCookie('accessToken'),
                }
            }
        )
            .catch((error) => console.error('Error:', error))
            .then(() => window.location.href = '/main');
    };

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
                <h2 className="text-xl font-bold mb-4">Search Results</h2>
                <ul className="space-y-4">
                    {/* {mockSearchResults.map((chat) => (
                        <li key={chat.id} className="bg-white rounded-lg shadow-md overflow-hidden">
                            <button
                                className="w-full text-left p-4 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-opacity-50"
                                onClick={() => setSelectedChat(chat)}
                            >
                                <h3 className="font-semibold text-lg">{chat.name}</h3>
                                <p className="text-xs text-gray-400">Last active: {chat.lastActive}</p>
                            </button>
                        </li>
                    ))} */}
                    {searchResults.length > 0 ? (
                        <ul className="space-y-4">
                            {searchResults.map((chat) => (
                                <ChatInfo  {...chat} setSelectedChat={setSelectedChat} />
                            ))}
                        </ul>
                    ) : (
                        <div className="text-center py-8">
                            <p className="text-gray-600">No chat rooms found for "{searchQuery}".</p>
                        </div>
                    )}
                </ul>
            </main>

            {/* Bottom Navigation Bar */}
            <NavigationBar activeTab='main'/>

            {/* Join Chat Popup */}
            {selectedChat && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-sm">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-xl font-bold">Join Chat</h2>
                            <button
                                onClick={() => setSelectedChat(null)}
                                className="text-gray-500 hover:text-gray-700 focus:outline-none"
                                aria-label="Close popup"
                            >
                                <X />
                            </button>
                        </div>
                        <p className="mb-6">
                            Do you want to join the chat room "{selectedChat.chatName}"?
                        </p>
                        <div className="flex justify-end space-x-4">
                            <button
                                onClick={() => setSelectedChat(null)}
                                className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-opacity-50"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleJoinChat}
                                className="px-4 py-2 bg-yellow-500 text-white rounded-md hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:ring-opacity-50"
                            >
                                Join Chat
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}