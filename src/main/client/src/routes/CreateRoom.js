import styles from "./RoomForm.module.css";
import axios from "axios";
import button_styles from "../components/Button.module.css";
import { validationSchema } from "../schemas/CreateRoomSchema";
import { useNavigate } from "react-router-dom";
import { useFormik } from "formik";
import NavBar from "../components/NavBar.js";

function CreateRoom() {
  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: {
      roomName: "",
      roomCode: "",
      capacity: "",
    },
    validateOnMount: true,
    validationSchema: validationSchema,
  });

  const createRoom = async (values) => {
    try {
      await axios.post("/classrooms", values);
      navigate("/home");
    } catch (error) {
      console.error("There has been an error login");
    }
  };

  return (
    <div className={styles.container}>
      <NavBar mode="default" />
      <div className={styles.wrapper}>
        <form
          onSubmit={(e) => {
            formik.handleSubmit(e);
            createRoom(formik.values);
          }}
          autoComplete="off"
        >
          <p className={styles.label}>방 이름</p>
          {formik.touched.roomName && formik.errors.roomName ? (
            <div className={styles.errorMsg}>{formik.errors.roomName}</div>
          ) : null}
          <input
            className={styles.input}
            type="text"
            name="roomName"
            value={formik.values.roomName}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          ></input>
          <p className={styles.label}>입장 코드</p>
          {formik.touched.roomCode && formik.errors.roomCode ? (
            <div className={styles.errorMsg}>{formik.errors.roomCode}</div>
          ) : null}
          <input
            className={styles.input}
            type="text"
            name="roomCode"
            value={formik.values.roomCode}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          ></input>
          <p className={styles.label}>정원</p>
          {formik.touched.capacity && formik.errors.capacity ? (
            <div className={styles.errorMsg}>{formik.errors.capacity}</div>
          ) : null}
          <input
            className={styles.input}
            type="text"
            name="capacity"
            value={formik.values.capacity}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          ></input>

          <button
            className={`${button_styles.btn} ${button_styles.room}`}
            type="submit"
            disabled={!formik.isValid || formik.isSubmitting}
          >
            방 생성
          </button>
        </form>
      </div>
    </div>
  );
}

export default CreateRoom;
