import { useState, useRef, useEffect } from 'react';
import { Send, MoreVertical, X, ArrowLeft } from 'lucide-react';
import { getCookie } from '../utils/Cookie';

export default function Chat() {
    const userChatId = new URLSearchParams(window.location.search).get('userChatId');
    if (userChatId === null) {
        window.location.href = '/main';
    }
    const SERVER_URL = 'http://localhost:8080';
    const CHAT_URI = '/chat/messages';
    
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [me, setMe] = useState({});
    const messagesEndRef = useRef(null);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(scrollToBottom, [messages]);

    useEffect(() => {
        fetch(SERVER_URL + CHAT_URI + '?userChatId=' + userChatId,
            {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + getCookie('accessToken'),
                }
            }
        )
            .then((response) => response.json())
            .then((data) => {
                data.forEach(element => {
                    messages.push(element);
                });
                setMessages([...messages]);
            })
            .catch((error) => {
                window.location.href = '/main';
            });
    }, []);

    useEffect(() => {
        fetch(SERVER_URL + '/user/me',
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
                setMe(data);
            });
    }, []);

    const handleSendMessage = (e) => {
        e.preventDefault();
        if (newMessage.trim()) {
            const msg = newMessage.trim();
            fetch(SERVER_URL + CHAT_URI + '?userChatId=' + userChatId + '&message=' + msg,
                {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + getCookie('accessToken'),
                    }
                }
            )
                .catch((error) => console.error('Error:', error))
                .then((response) => response.json())
                .then((data) => {
                    setMessages([...data]);
                    setNewMessage('');
                });
        }
    };

    const handleLeaveChat = () => {
        console.log('Leaving chat');
        setIsMenuOpen(false);
        // Implement leave chat logic here
    };

    const handleRemoveChat = () => {
        console.log('Removing chat');
        setIsMenuOpen(false);
        // Implement remove chat logic here
    };
    
    const handleBackToMain = () => {
        window.location.href = '/main';
    };

    return (
        <div className="flex flex-col h-screen bg-gray-100">
            {/* Chat Header */}
            <header className="bg-white shadow-md p-4 flex justify-between items-center sticky top-0 z-10">
                <div className="flex items-center">
                    <button
                        onClick={handleBackToMain}
                        className="mr-4 text-gray-600 hover:text-gray-800 focus:outline-none focus:text-gray-800"
                        aria-label="Back to main menu"
                    >
                        <ArrowLeft />
                    </button>
                    <h1 className="text-xl font-bold text-gray-800">Chat Room</h1>
                </div>
                <button
                    onClick={() => setIsMenuOpen(true)}
                    className="text-gray-600 hover:text-gray-800 focus:outline-none focus:text-gray-800"
                    aria-label="Chat options"
                >
                    <MoreVertical />
                </button>
            </header>

            {/* Messages Area */}
            <main className="flex-grow overflow-y-auto p-4 flex flex-col-reverse">
                <div ref={messagesEndRef} />
                {messages.slice().reverse().map((message) => (
                    <div key={message.id} className={`mb-4 ${message.userId === me.id ? 'text-right' : 'text-left'}`}>
                        <div className={`inline-block p-2 rounded-lg ${message.userId === me.id ? 'bg-yellow-200' : 'bg-white'}`}>
                            <p className="font-semibold">{message.userName}</p>
                            <p>{message.message}</p>
                            <p className="text-xs text-gray-500 mt-1">
                                {new Date(message.insertTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            </p>
                        </div>
                    </div>
                ))}
            </main>

            {/* Message Input */}
            <form onSubmit={handleSendMessage} className="bg-white shadow-md p-4 sticky bottom-0 z-10 flex">
                <input
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    placeholder="Type a message..."
                    className="flex-grow p-2 border border-gray-300 rounded-l-md focus:outline-none focus:ring-2 focus:ring-yellow-400"
                    aria-label="Type a message"
                />
                <button
                    type="submit"
                    className="bg-yellow-400 p-2 rounded-r-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-yellow-600"
                    aria-label="Send message"
                >
                    <Send className="text-white" />
                </button>
            </form>

            {/* Chat Options Menu */}
            {isMenuOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-sm">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-xl font-bold">Chat Options</h2>
                            <button
                                onClick={() => setIsMenuOpen(false)}
                                className="text-gray-500 hover:text-gray-700 focus:outline-none"
                                aria-label="Close menu"
                            >
                                <X />
                            </button>
                        </div>
                        <div className="space-y-4">
                            <button
                                onClick={handleLeaveChat}
                                className="w-full bg-yellow-400 text-white py-2 rounded-md hover:bg-yellow-500 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-opacity-50"
                            >
                                Leave Chat
                            </button>
                            <button
                                onClick={handleRemoveChat}
                                className="w-full bg-red-500 text-white py-2 rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50"
                            >
                                Remove Chat
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}