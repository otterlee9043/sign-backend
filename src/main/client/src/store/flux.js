const getState = ({ getStore, getActions, setStore }) => {
  return {
    store: {
      token: null,
      message: null,
      demo: [
        {
          title: "FIRST",
          background: "white",
          initial: "white",
        },
        {
          title: "SECOND",
          background: "white",
          initial: "white",
        },
      ],
    },
    actions: {
      // Use getActions to call a function within a fuction
      exampleFunction: () => {
        getActions().changeColor(0, "green");
      },
      syncTokenFromSessionStore: () => {
        const token = sessionStorage.getItem("token");
        if (token && token != "" && token != undefined) setStore({ token: token });
      },
      signup: async (username, email, password) => {
        const opts = {
          method: "POST",
          body: JSON.stringify({
            username: username,
            email: email,
            password: password,
          }),
          headers: new Headers({
            "content-type": "application/json",
          }),
        };
        try {
          const response = await fetch("/api/member/join", opts);
          if (response.status !== 200) {
            alert("There has been some errors.");
            return false;
          }
          const data = await response.json();
          console.log("This came from the backend", data);
          //sessionStorage.setItem("token", data.access_token);
          //setStore({ token: data.access_token });
          return true;
        } catch (error) {
          console.error("There has been an error login", error);
        }
      },
      login: async (email, password) => {
        const opts = {
          method: "POST",
          body: JSON.stringify({
            email: email,
            password: password,
          }),
          headers: new Headers({
            "content-type": "application/json",
          }),
        };
        try {
          const response = await fetch("/api/token", opts);
          if (response.status !== 200) {
            alert("There has been some errors.");
            return false;
          }
          const data = await response.json();
          console.log("This came from the backend", data);
          sessionStorage.setItem("token", data.access_token);
          setStore({ token: data.access_token });
          return true;
        } catch (error) {
          console.error("There has been an error login");
        }
      },
      logout: () => {
        sessionStorage.removeItem("token");
        console.log("Login out");
        setStore({ token: null });
      },
      getMessage: () => {
        const store = getStore();
        const opts = {
          headers: {
            Authorization: "Bearer " + store.token,
          },
        };
        // fetching data from the backend
        fetch("/api/messages", opts)
          .then((resp) => resp.json())
          .then((data) => {
            setStore({ message: data.message });
            console.log(data.message);
          })
          .catch((error) => console.log("Error loading message from backend", error));
      },
      changeColor: (index, color) => {
        //get the store
        const store = getStore();

        //we have to loop the entire demo array to look for the respective index
        //and change its color
        const demo = store.demo.map((elm, i) => {
          if (i === index) elm.background = color;
          return elm;
        });

        //reset the global store
        setStore({ demo: demo });
      },
    },
  };
};

export default getState;
