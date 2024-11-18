import { useEffect, useState } from 'react';
import { AlertTriangle, X, LogOut } from 'lucide-react';
import NavigationBar from '../components/NavigationBar';
import { getCookie, removeCookie } from '../utils/Cookie';

export default function MyPage() {

    const SERVER_URL = 'http://localhost:8080/api';
    const USER_URI = '/user/me';

    const [user, setUser] = useState({
        nickname: '',
        insertTime: '',
    });
    const [isDeletePopupOpen, setIsDeletePopupOpen] = useState(false);

    const handleDeleteAccount = () => {
        // Implement account deletion logic here
        console.log("Account deleted");
        fetch(SERVER_URL + USER_URI,
            {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + getCookie('accessToken'),
                }
            }
        )
            .catch((error) => console.error('Error:', error))
            .then(() => window.location.href = '/');
    };

    const handleLogout = () => {
        removeCookie('accessToken');
        removeCookie('refreshToken');
        window.location.href = '/';
    };

    useEffect(() => {
        fetch(SERVER_URL + USER_URI,
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
                setUser(data);
            });
    }, []);

    return (
        <div className="flex flex-col h-screen bg-gray-100">
            {/* Header */}
            <header className="bg-white shadow-md p-4 sticky top-0 z-10">
                <h1 className="text-2xl font-bold text-gray-800">My Page</h1>
            </header>

            {/* Main Content Area */}
            <main className="flex-grow overflow-y-auto p-4">
                <div className="bg-white rounded-lg shadow-md p-6 mb-6">
                    <h2 className="text-xl font-semibold mb-4">User Information</h2>
                    <div className="space-y-2">
                        <p><span className="font-medium">Nickname:</span> {user.nickname}</p>
                        <p><span className="font-medium">Signup Date:</span> {new Date(user.insertTime).toLocaleDateString()}</p>
                    </div>
                </div>

                <div className="space-y-4">
                    <button
                        onClick={handleLogout}
                        className="w-full bg-yellow-500 text-white py-3 rounded-md hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:ring-opacity-50 transition duration-300 flex items-center justify-center"
                    >
                        <LogOut className="mr-2" size={20} />
                        Logout
                    </button>

                    <button
                        onClick={() => setIsDeletePopupOpen(true)}
                        className="w-full bg-red-500 text-white py-3 rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50 transition duration-300"
                    >
                        Delete Account
                    </button>
                </div>
            </main>

            {/* Bottom Navigation Bar */}
            <NavigationBar activeTab='mychat' />

            {/* Delete Account Popup */}
            {isDeletePopupOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-sm">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-xl font-bold text-red-600 flex items-center">
                                <AlertTriangle className="mr-2" /> Warning
                            </h2>
                            <button
                                onClick={() => setIsDeletePopupOpen(false)}
                                className="text-gray-500 hover:text-gray-700 focus:outline-none"
                                aria-label="Close popup"
                            >
                                <X />
                            </button>
                        </div>
                        <p className="mb-6 text-gray-700">
                            Are you sure you want to delete your account? This action cannot be reversed, and all your data will be permanently lost.
                        </p>
                        <div className="flex justify-end space-x-4">
                            <button
                                onClick={() => setIsDeletePopupOpen(false)}
                                className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-opacity-50"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleDeleteAccount}
                                className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50"
                            >
                                Delete Account
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}