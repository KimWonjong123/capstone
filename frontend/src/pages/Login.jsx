import '../output.css';
import { useState, useEffect } from 'react';
import kakaoLoginImage from '../static/kakao_login_medium_narrow.png';

export default function Login() {
  const handleKakaoLogin = () => {
    window.location.href = kakaoUrl;
  };

  const SERVER_URL = "http://150.230.255.50/api";
  const LOGIN_URI = "/auth/oauth/kakao/url";
  const [kakaoUrl, setKakaoUrl] = useState();

  useEffect(() => {
    fetch(SERVER_URL + LOGIN_URI)
      .catch((error) => console.error("Error:", error))
      .then(response => response.text())
      .then((url) => {
        setKakaoUrl(url);
        console.log(url);
      });
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-b from-yellow-100 to-yellow-200 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-xl p-8 space-y-8">
        <div className="text-center">
          <h1 className="mt-4 text-2xl font-bold text-gray-900">Group Food Delivery</h1>
          <p className="mt-2 text-sm text-gray-600">Order together, save money!</p>
        </div>

        <button
          onClick={handleKakaoLogin}
          className='flex w-full'
        >
          <img src={kakaoLoginImage} alt="kakao login button"
            className='mx-auto'
          />
        </button>

        <p className="text-center text-xs text-gray-600">
          By logging in, you agree to our Terms of Service and Privacy Policy.
        </p>
      </div>
    </div>
  );
}