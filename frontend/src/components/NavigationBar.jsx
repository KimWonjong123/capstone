import { Home, MessageSquare, User } from 'lucide-react';

export default function NavigationBar(props) {

    const activeTab = props.activeTab;

    const handleNavClick = (tab) => {
        console.log(`Navigate to ${tab}`);
        window.location.href = `/${tab.toLowerCase()}`;
    }

    return (
        <nav className="bg-white shadow-md sticky bottom-0 z-10">
        <ul className="flex justify-around p-4">
          <li>
                    <button className={`flex flex-col items-center ${activeTab.toLowerCase() === 'home' ? 'text-yellow-500' : 'text-gray-600'} focus:outline-none focus:text-yellow-600`}
                        aria-label="Home"
                        onClick={() => handleNavClick('main')}
                    >
              <Home />
              <span className="text-xs mt-1">Home</span>
            </button>
          </li>
          <li>
                    <button className={`flex flex-col items-center ${activeTab.toLowerCase() === 'chat' ? 'text-yellow-500' : 'text-gray-600'} focus:outline-none focus:text-yellow-600`}
                        aria-label="My Chats"
                        onClick={() => handleNavClick('mychat')}
                    >
              <MessageSquare />
              <span className="text-xs mt-1">My Chats</span>
            </button>
          </li>
          <li>
                    <button className={`flex flex-col items-center ${activeTab.toLowerCase() === 'mypage' ? 'text-yellow-500' : 'text-gray-600'} focus:outline-none focus:text-yellow-600`}
                        aria-label="My Page"
                        onClick={() => handleNavClick('mypage')}
                    >
              <User />
              <span className="text-xs mt-1">My Page</span>
            </button>
          </li>
        </ul>
      </nav>
    )
}