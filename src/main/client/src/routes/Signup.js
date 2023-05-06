import React, { useEffect } from "react";
import styles from "./Login.module.css";
import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Context } from "../store/appContext";
import { useFormik } from "formik";
import axios from "axios";
import * as Yup from "yup";

function Signup() {
  const { store, actions } = useContext(Context);
  const [hasServerError, setHasServerError] = useState(false);
  const [serverError, setServerError] = useState({});
  const usernameSchema = Yup.string()
    .required("이름을 입력하세요.")
    .min(2, "이름은 최소 2글자 이상입니다.")
    .max(10, "이름은 최대 10글자입니다.")
    .matches(
      /^[가-힣a-zA-Z][^!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?\s]*$/,
      "이름에는 특수문자를 포함할 수 없고 숫자로 시작할 수 없습니다."
    );

  const emailSchema = Yup.string()
    .required("이메일을 입력하세요.")
    .email("올바른 이메일 형식이 아닙니다.");

  const validationSchema = Yup.object().shape({
    username: usernameSchema.test("username", "사용 중인 이름입니다.", async (username) => {
      if (await usernameSchema.isValid(username)) {
        const res = await fetch(`/api/member/username/${username}/exists`);
        const data = await res.json();
        const result = data["duplicate"];
        return !result;
      }
      return true;
    }),
    email: emailSchema.test("email", "사용 중인 이메일입니다.", async (email) => {
      if (await emailSchema.isValid(email)) {
        const res = await fetch(`/api/member/email/${email}/exists`);
        const data = await res.json();
        const result = data["duplicate"];
        return !result;
      }
      return true;
    }),
    password: Yup.string()
      .required("비밀번호를 입력하세요.")
      .min(8, "비밀번호는 최소 8자리 이상입니다")
      .max(16, "비밀번호는 최대 16자리입니다.")
      .matches(
        /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[^\s]*$/,
        "알파벳, 숫자, 공백을 제외한 특수문자를 모두 포함해야 합니다."
      ),
    password2: Yup.string()
      .required("비밀번호를 다시 입력하세요.")
      .oneOf([Yup.ref("password"), null], "비밀번호가 일치하지 않습니다!"),
  });

  const formik = useFormik({
    initialValues: {
      email: "",
      username: "",
      password: "",
      password2: "",
    },
    validateOnMount: true,
    validationSchema: validationSchema,
  });

  const navigate = useNavigate();

  const handleClick = async (values) => {
    const { username, email, password, password2 } = values;
    const opts = {
      method: "POST",
      body: JSON.stringify({
        username: username,
        email: email,
        password: password,
        password2: password2,
      }),
      headers: new Headers({
        "content-type": "application/json",
      }),
    };
    try {
      const response = await fetch("/api/member/join", opts);
      const data = await response.json();
      if (response.status === 200) {
        navigate("/");
      } else if (response.status === 400) {
        // validation error가 담긴 객체를
        // yup.test 해서
        console.log("response.status === 400");
        console.log(data);
      }
      console.log("This came from the backend", data);
      return true;
    } catch (error) {
      console.error("There has been an error login", error);
    }
  };

  return (
    <div>
      <div className={styles.wrapper}>
        <div className={styles.login}>
          <span className={styles.loginText}>Sign up</span>
          <form
            onSubmit={(e) => {
              formik.handleSubmit(e);
              handleClick(formik.values);
            }}
            autoComplete="off"
          >
            {formik.touched.username && formik.errors.username ? (
              <p className={styles["input-error"]}>{formik.errors.username}</p>
            ) : null}
            <input
              className={styles.input}
              type="text"
              name="username"
              placeholder="Username"
              value={formik.values.username}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            />
            {formik.touched.email && formik.errors.email ? (
              <p className={styles["input-error"]}>{formik.errors.email}</p>
            ) : null}
            <input
              className={styles.input}
              type="text"
              name="email"
              placeholder="Email"
              value={formik.values.email}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            />
            {formik.touched.password && formik.errors.password ? (
              <p className={styles["input-error"]}>{formik.errors.password}</p>
            ) : null}
            <input
              className={styles.input}
              type="password"
              name="password"
              placeholder="Password"
              value={formik.values.password}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            />
            {formik.touched.password2 && formik.errors.password2 ? (
              <p className={styles["input-error"]}>{formik.errors.password2}</p>
            ) : null}
            <input
              className={styles.input}
              type="password"
              name="password2"
              placeholder="Password Confirmation"
              value={formik.values.password2}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            />
            <button
              className={
                !formik.isValid || formik.isSubmitting
                  ? `${styles.loginBtn} ${styles.disabled}`
                  : styles.loginBtn
              }
              type="submit"
              // disabled={!formik.isValid || formik.isSubmitting}
            >
              Sign up
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default Signup;
