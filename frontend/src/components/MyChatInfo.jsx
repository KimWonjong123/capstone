import { ChevronRight } from 'lucide-react';

export default function MyChatInfo(props) {

    return (
        <li key={props.chatId} className="bg-white rounded-lg shadow-md overflow-hidden">
            <button
                className="w-full text-left p-4 flex items-center justify-between hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-opacity-50"
                onClick={() => console.log(`Navigate to chat ${props.chatId}`)}
            >
                <div>
                    <h2 className="font-semibold text-lg text-gray-800">{props.chatName}</h2>
                    <span className="text-xs text-gray-400 mt-1">{props.insertTime}</span>
                </div>
                <ChevronRight className="text-gray-400" />
            </button>
        </li>
    )
}