import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./routes/Home.js";
import Classroom from "./routes/Classroom.js";
import Login from "./routes/Login.js";
import Signup from "./routes/Signup.js";
import Main from "./routes/Main.js";
import injectContext from "./contexts/CurrentUserContext.js";
import CreateRoom from "./routes/CreateRoom.js";
import EnterRoom from "./routes/EnterRoom.js";
import MyPage from "./routes/MyPage.js";

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path="/home" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/classroom/:roomId" element={<Classroom />} />
        <Route path="/createroom" element={<CreateRoom />} />
        <Route path="/enterroom" element={<EnterRoom />} />
        <Route path="/mypage" element={<MyPage />} />
      </Routes>
    </BrowserRouter>
  );
};

export default injectContext(App);
