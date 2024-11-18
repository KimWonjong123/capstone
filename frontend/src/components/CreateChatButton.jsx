import { useState } from "react";
import { X } from "lucide-react";
import { getCookie } from "../utils/Cookie";

export default function CreateChatButton() {

    const SERVER_URL = "http://localhost:8080/api";
    const CHAT_URI = "/chat";
    
    const [isPopupOpen, setIsPopupOpen] = useState(false);
    const [newChatName, setNewChatName] = useState('');

    const handleCreateChat = (e) => {
        e.preventDefault();
        fetch(SERVER_URL + CHAT_URI,
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + getCookie('accessToken'),
                },
                body: JSON.stringify({ name: newChatName })
            }
        )
            .catch((error) => console.error('Error:', error))
            .then(() => window.location.href = '/myChat');
    };
    
    return (
        <div>

            {/* Add Chat Room Button */}
            <button
                className="fixed bottom-20 right-4 bg-yellow-500 text-white p-4 rounded-full shadow-lg hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-opacity-50 text-sm"
                aria-label="Create new chat room"
                onClick={() => setIsPopupOpen(true)}
            >
                +
            </button>

            {/* Popup for creating new chat room */}
            {isPopupOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-sm">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-xl font-bold">Create New Chat Room</h2>
                            <button
                                onClick={() => setIsPopupOpen(false)}
                                className="text-gray-500 hover:text-gray-700 focus:outline-none"
                                aria-label="Close popup"
                            >
                                <X />
                            </button>
                        </div>
                        <form onSubmit={handleCreateChat}>
                            <input
                                type="text"
                                placeholder="Enter chat room name"
                                value={newChatName}
                                onChange={(e) => setNewChatName(e.target.value)}
                                className="w-full p-2 border border-gray-300 rounded-md mb-4 focus:outline-none focus:ring-2 focus:ring-yellow-400"
                                aria-label="New chat room name"
                            />
                            <button
                                type="submit"
                                className="w-full bg-yellow-500 text-white p-2 rounded-md hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-opacity-50"
                            >
                                Create Chat Room
                            </button>
                        </form>
                    </div>
                </div>
            )}

        </div>
    );
}