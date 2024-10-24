import React, { useState } from 'react';
import { Search, Home, MessageSquare, User } from 'lucide-react';
import NavigationBar from '../components/NavigationBar';

export default function MainPage() {
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    // Implement search functionality here
    console.log('Searching for:', searchQuery);
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
        <h1 className="text-2xl font-bold mb-4">Group Food Delivery</h1>
        <p className="mb-4">Find or create a chat room to order food together!</p>
        
        {/* Placeholder for chat room list or other content */}
        <div className="space-y-4">
          {[1, 2, 3].map((item) => (
            <div key={item} className="bg-white p-4 rounded-md shadow">
              <h2 className="font-semibold">Chat Room {item}</h2>
              <p className="text-sm text-gray-600">Join this room to order food together!</p>
            </div>
          ))}
        </div>

        {/* Add Chat Room Button */}
        <button
          className="fixed bottom-20 right-4 bg-yellow-500 text-white p-4 rounded-full shadow-lg hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-opacity-50"
          aria-label="Create new chat room"
        >
          +
        </button>
      </main>

      {/* Bottom Navigation Bar */}
          <NavigationBar
              activeTab="home"
          />
    </div>
  );
}