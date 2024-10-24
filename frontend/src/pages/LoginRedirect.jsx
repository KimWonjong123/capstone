import '../output.css';
import { useEffect } from 'react';
import { setCookie } from '../utils/Cookie';

export default function LoginRedirect() {
    const SERVER_URL = "http://localhost:8080";
    const REDIRECT_URL = "/auth/oauth/kakao";
    const code = new URLSearchParams(window.location.search).get('code');
    
    useEffect(() => {
        if (code) {
            fetch(SERVER_URL + REDIRECT_URL + "?code=" + code)
                .catch((error) => console.error("Error:", error))
                .then(response => response.json())
                .then((data) => {
                    setCookie("accessToken", data.accessToken);
                    setCookie("refreshToken", data.refreshToken);
                    console.log(data);
                    window.location.href = "/main";
                });
        }
    }, [code]);

    console.log(SERVER_URL + REDIRECT_URL + "?code=" + code);

    return (
    <div className="min-h-screen bg-gradient-to-b from-yellow-100 to-yellow-200 flex flex-col items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-xl p-8 max-w-sm w-full text-center">
        <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-yellow-500 mx-auto"></div>
        <h2 className="mt-4 text-xl font-semibold text-gray-700">Logging in with Kakao</h2>
        <p className="mt-2 text-sm text-gray-600">Please wait while we complete the process...</p>
      </div>
    </div>
  );
};