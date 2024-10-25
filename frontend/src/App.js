import { Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import LoginRedirect from "./pages/LoginRedirect";
import Main from "./pages/Main";
import MyChat from "./pages/MyChat";
import MyPage from "./pages/MyPage";
import SearchResults from "./pages/Search";

function App() {
    return (
        <div className="App">
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/oauth/kakao" element={<LoginRedirect />} />
                <Route path="/main" element={<Main />} />
                <Route path="/mychat" element={<MyChat />} />
                <Route path="/mypage" element={<MyPage />} />
                <Route path="/search" element={<SearchResults />} />
            </Routes>
        </div>
    );
}

export default App;
