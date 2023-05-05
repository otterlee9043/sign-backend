import React from "react";
import styles from "./Login.module.css";
import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Context } from "../store/appContext";
import { Formik, Form, Field, ErrorMessage, useFormik } from "formik";
import SignupValidationMessage from "../components/SignupValidationMessage.js";
import axios from "axios";
import * as Yup from "yup";

function Signup() {
  const { store, actions } = useContext(Context);

  const usernameSchema = Yup.string()
    .required("닉네임을 입력하세요!")
    .min(2, "닉네임은 최소 2글자 이상입니다!")
    .max(10, "닉네임은 최대 10글자입니다!")
    .matches(
      /^[가-힣a-zA-Z][^!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?\s]*$/,
      "닉네임에 특수문자가 포함되면 안되고 숫자로 시작하면 안됩니다!"
  );
  
  const emailSchema = Yup.string()
    .required("이메일을 입력하세요!")
    .email("올바른 이메일 형식이 아닙니다!");
  
  const usernameDuplicateCheck = Yup.string().test(
    "Unique Username",
    "Username already in use",
    async (username) => {
      if (await usernameSchema.isValid(username)) {
        // return !(await (await fetch(`/api/member/username/${username}/exists`)).json()[
        //   "duplicate"
        // ]);
        const res = await fetch(`/api/member/username/${username}/exists`);
        console.log(res);
        const data = await res.json();
        console.log(data);
        const result = data[
          "duplicate"
        ];
        console.log(result);
        if (result)
          return false;
        return true;
      }
      return true;
    }
  );

  const emailDuplicateCheck = Yup.string().test("Unique Email", "Email already in use", async (email) => {
    if (await emailSchema.isValid(email)) {
      return !(await (await fetch(`/api/member/email/${email}/exists`)).json()["duplicate"]);
    }
    return true;
  });



  const validationSchema = Yup.object().shape({
    email: emailSchema,
    username: usernameSchema,
    password: Yup.string()
      .required("패스워드를 입력하세요!")
      .min(8, "비밀번호는 최소 8자리 이상입니다")
      .max(16, "비밀번호는 최대 16자리입니다!")
      .matches(
        /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[^\s]*$/,
        "알파벳, 숫자, 공백을 제외한 특수문자를 모두 포함해야 합니다!"
      ),
    password2: Yup.string()
      .required("필수 입력 값입니다!")
      .oneOf([Yup.ref("password"), null], "비밀번호가 일치하지 않습니다!"),
  });

  const formik = useFormik({
    initialValues: {
      email: "",
      username: "",
      password: "",
      password2: "",
    },
    validationSchema: validationSchema,
  });

  const navigate = useNavigate();

  const handleClick = async (values) => {
    const { username, email, password, password2 } = values;
    const opts = {
      url: "/api/member/join",
      method: "POST",
      headers: {
        "content-type": "application/json",
      },
      data: {
        username: username,
        email: email,
        password: password,
        password2: password2,
      },
    };
    try {
      const response = await axios(opts);
      const data = response.data;
      if (response.status === 200) {
        navigate("/");
      } else if (response.status === 400) {
        // server-side error
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
          {store.token && store.token != "" && store.token != undefined ? (
            <div>You are logged in with {store.token}</div>
          ) : (
            <form
              onSubmit={(e) => {
                formik.handleSubmit(e);
                handleClick(values);
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
                  onBlur={async (e) => {
                    formik.handleBlur(e);
                    await usernameDuplicateCheck.validate(formik.values.username);
                  }}
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
                onBlur={async (e) => {
                  formik.handleBlur(e);
                  await emailDuplicateCheck.validate(formik.values.email);
                  console.log(formik.errors.email);
                }}
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
                className={styles.loginBtn}
                type="submit"
                disabled={!formik.isValid || formik.isSubmitting}
              >
                Sign up
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}

export default Signup;
