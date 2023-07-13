import * as Yup from "yup";
import axios from "axios";

const roomCodeSchema = Yup.string().required("입장 코드를 입력하세요.");

export const validationSchema = Yup.object().shape({
  roomName: Yup.string()
    .required("방 이름을 입력하세요.")
    .min(2, "방 이름은 최소 2글자 이상입니다.")
    .max(30, "방 이름은 최대 30글자입니다."),
  roomCode: roomCodeSchema.test("roomName", "사용 중인 입장 코드입니다.", async (roomCode) => {
    if (await roomCodeSchema.isValid(roomCode)) {
      const response = await axios.get(`/classroom/roomCode/${roomCode}/exists`);
      const result = response.data["duplicate"];
      return !result;
    }
  }),
  capacity: Yup.number("숫자를 입력하세요.")
    .required("정원을 입력하세요")
    .positive("정원은 1명 이상입니다.")
    .max(100, "정원은 100명을 넘을 수 없습니다."),
});
