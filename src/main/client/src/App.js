import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./routes/Home.js";
import Classroom from "./routes/Classroom.js";
import MyRoom from "./routes/MyRoom.js";
import Login from "./routes/Login.js";
import Signup from "./routes/Signup.js";
import Main from "./routes/Main.js";
import Confirmed from "./routes/Confirmed.js";
import injectContext from "./store/appContext";
import CreateRoom from "./routes/CreateRoom.js";
import EnterRoom from "./routes/EnterRoom.js";

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/main" element={<Main />} />
        <Route path="/classroom/:roomId" element={<Classroom />} />
        <Route path="/mystate" element={<MyRoom />} />
        <Route path="/confirm/:token" element={<Confirmed />} />
        <Route path="/createroom" element={<CreateRoom />} />
        <Route path="/enterroom" element={<EnterRoom />} />
      </Routes>
    </BrowserRouter>
  );
};

export default injectContext(App);
