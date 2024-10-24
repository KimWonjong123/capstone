import { Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import LoginRedirect from "./pages/LoginRedirect";
import Main from "./pages/Main";

function App() {
    return (
        <div className="App">
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/oauth/kakao" element={<LoginRedirect />} />
                <Route path="/main" element={<Main />} />
            </Routes>
        </div>
    );
}

export default App;
