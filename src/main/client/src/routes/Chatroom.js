import { useParams } from "react-router-dom";
import NavBar from "../components/NavBar.js";

function Chatroom() {
  const params = useParams();
  const roomId = parseInt(params.roomId);
  return (
    <div>
      <NavBar mode="chatroom" roomId={roomId} />
      <h3>채팅방</h3>
      <div></div>
    </div>
  );
}

export default Chatroom;
