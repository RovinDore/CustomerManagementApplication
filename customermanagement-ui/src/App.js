import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './components/pages/CustomerList';
import Custom404 from './components/pages/Custom404'
import Customer from './components/pages/Customer';
import Login from './components/pages/Login';
import ForgetPassword from './components/pages/ForgetPassword';
import ResetPassword from './components/pages/ResetPassword';
import TopMenu from './components/layout/TopMenu';
import { useEffect, useState } from 'react';
import { checkLogin } from './components/scripts/checkLogin'
import { LinearProgress } from '@mui/material';
import Projects from './components/pages/ProjectList';
import { Box } from '@mui/system';
import Profile from './components/pages/Profile';
import CreateProfile from './components/pages/CreateProfile';

function App() {
	const [loggedIn, setLoggedIn] = useState(null);
	const [gotUserData, setUserData] = useState(false);
	const [user, setUser] = useState("USER")

	useEffect(() => {
		if (loggedIn === null) {
			let { authenticated, user } = checkLogin();
			if (!authenticated) setLoggedIn(false);
			else { setLoggedIn(true); setUser(user) };

			setUserData(true);
		}
		//   else setLoggedIn(false);
	}, [loggedIn]);

	const logOut = () => {
		localStorage.removeItem("jwt");
		setLoggedIn(false);
		// console.log('hii', window.location)
		if (window.location.pathname !== '/') window.location = '/';
	}

	const authenticateUser = (jwt, user) => {
		console.log({ jwt, user });
		if (jwt === null || jwt === undefined) return;
		let loginCache = { time: new Date().getTime(), token: jwt, user };
		setUser(user);
		console.log({ loginCache });
		if (typeof (Storage) !== "undefined") localStorage.setItem("jwt", JSON.stringify(loginCache));
		console.log('authenticated');
		setLoggedIn(true);
		if (window.location.pathname !== '/') window.location = '/';
	}

	if (!gotUserData) return <LinearProgress />

	return (
		<Router>
			<div className="App" style={{ backgroundColor: 'darkgrey' }}>
				{
					!loggedIn &&
					<Routes>
						<Route path="/" exact element={<Login logIn={authenticateUser} />} />
						<Route path="/sign-up" exact element={<CreateProfile logIn={authenticateUser} />} />
						<Route path="/forget-password" exact element={<ForgetPassword />} />
						<Route path="/reset-password/:keyCode/:userId" exact element={<ResetPassword />} />
						<Route path="*" exact element={<Custom404 />} />
					</Routes>
				}
				{
					loggedIn && user.active &&
					<>
						<TopMenu logOut={logOut} userRole={user.authorities} />
						<Box sx={{ paddingBottom: 8, minHeight: 'calc(100vh - 108.8px)' }}>
							<Routes>
								<Route path="/" exact element={<Home />} />
								<Route path="/customer/:customerId" exact element={<Customer />} />
								<Route path="/projects" exact element={<Projects />} />
								<Route path="/profile" exact element={<Profile />} />
								<Route path="*" exact element={<Custom404 />} />
							</Routes>
						</Box>
					</>
				}
			</div>
		</Router>
	);
}

export default App;
