import styles from "./Login.module.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useFormik } from "formik";
import { validationSchema } from "../schemas/SignupSchema";

function Signup() {
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
    try {
      const response = await axios.post("/member/join", values);
      navigate("/login");
    } catch (error) {
      if (error.response) {
        const data = error.response.data;
        formik.setErrors(data.errors);
      }
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
              className={`${styles["login-button"]} ${styles["login-button-blue"]}`}
              type="submit"
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
