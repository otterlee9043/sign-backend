import styles from "./RoomForm.module.css";
import Button from "../components/Button";
import { validationSchema } from "../schemas/CreateRoomSchema";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { useFormik } from "formik";

function CreateRoom() {
  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: {
      roomName: "",
      roomCode: "",
      capacity: "",
    },
    validateOnMount: false,
    validationSchema: validationSchema,
  });

  const createRoom = async (values) => {
    const { roomName, roomCode, capacity } = values;
    const opts = {
      method: "POST",
      body: JSON.stringify({
        roomName: roomName,
        roomCode: roomCode,
        capacity: capacity,
      }),
      headers: new Headers({
        "content-type": "application/json",
      }),
    };
    try {
      const response = await fetch("/api/classrooms", opts);
      if (response.ok) {
        navigate("/home");
      }
      if (response.status !== 200) {
        alert("There has been some errors.");
        return false;
      }
    } catch (error) {
      console.error("There has been an error login");
    }
  };

  return (
    <div className={styles.container}>
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
          <Button text="방 생성" type="room" handleClick={createRoom} />
        </form>
      </div>
    </div>
  );
}

export default CreateRoom;
