import React, { useState } from 'react'
import apiReq from '../scripts/apiReq'
import { Box, Typography, Grid, TextField, Collapse, Alert, AlertTitle, LinearProgress } from '@mui/material'
import Card from '@mui/material/Card';
// import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import LoadingButton from '@mui/lab/LoadingButton';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import Custom404 from './Custom404';
import { Link } from 'react-router-dom'

export default function CreateProfile(props) {
    const [myUser, setMyUser] = useState({
        username: '',
        email: '',
        password: '',
        phoneNumber: '',
        confirmpassword: ''

    });
    const [showStatus, setStatusMsg] = useState({ show: false, msg: '', title: '', severity: 'info' });
    const [isSubmitting, setisSubmitting] = useState(false);

    if (myUser === null) return <LinearProgress />;
    if (!myUser) return <Custom404 />

    const { username, email, password, confirmpassword } = myUser;

    const validateForm = () => {
        if (email === "" || email === null || email === undefined) return false;
        if (username === "" || username === null || username === undefined) return false;

        if (password === "" || password === null || password === undefined) return false;
        if (confirmpassword === "" || confirmpassword === null || confirmpassword === undefined) return false;
        if (password !== confirmpassword) {
            setStatusMsg({ show: true, msg: 'Passwords does not match!', severity: 'error', title: 'Error' });
            return false;
        }

        return true;
    }

    const handleSubmit = (e) => {
        setisSubmitting(true);
        setStatusMsg({ show: false });
        e.preventDefault();
        let url = '/auth/create/';

        if (!validateForm()) {
            setisSubmitting(false);
            return
        }

        console.log({ myUser });
        let dataToSend = myUser;
        dataToSend.userName = username;
        apiReq.post(url, dataToSend).then(access => {
            if (access.error === undefined && access.jwt !== undefined) props.logIn(access.jwt, access.user);
            else {
                setStatusMsg({ show: true, msg: 'Something went wrong', severity: 'error' });
                setisSubmitting(false);
            }
            // setMyUser(null);
        }).catch(resp => {
            console.log({ resp });
            setStatusMsg({ show: true, msg: 'Something went wrong creating profile', severity: 'error', title: 'Error' });
            setisSubmitting(false);
        });
    }

    const handleChange = (e) => {
        // console.log('change', e.target.name, e.target.value);
        setMyUser({ ...myUser, [e.target.name]: e.target.value.trim() })
    }

    return (
        <Box sx={{ backgroundColor: 'darkgrey', minHeight: '102vh', paddingBottom: 0 }}>
            <Grid container spacing={2} style={{ paddingTop: '5%' }}>
                <Grid item xs={2} md={3} lg={4}></Grid>
                <Grid item xs={8} md={6} lg={4} textAlign='center'>
                    <form autoComplete="off" onSubmit={handleSubmit}>
                        <Card>
                            <CardContent>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <Collapse in={showStatus.show}>
                                            <Alert severity={showStatus.severity} >
                                                <AlertTitle>{showStatus.title}</AlertTitle>
                                                {showStatus.msg}
                                            </Alert>
                                        </Collapse>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <Typography variant='h5' align='center'>Create Profile</Typography>
                                    </Grid>
                                    <Grid container item>
                                        <Grid container item xs={12} rowSpacing={2}>
                                            <Grid item xs={12}>
                                                <TextField required fullWidth label="Username" name='username' defaultValue={username} onChange={handleChange} />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField required fullWidth type="email" label="Email" name='email' defaultValue={email} onChange={handleChange} />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField type={'password'} required fullWidth name="password" label='Password' defaultValue={password} onChange={handleChange} />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField type={'password'} required fullWidth name="confirmpassword" label='Confirm Password' defaultValue={confirmpassword} onChange={handleChange} />
                                            </Grid>
                                            <Grid item xs={12} style={{ justifyContent: 'center' }}>
                                                <LoadingButton loading={isSubmitting} disabled={isSubmitting} color='success' variant="contained" type='submit' startIcon={<PersonAddIcon />}>Create Profile</LoadingButton>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Link to='/'>Login</Link> |
                                                <Link to='/forget-password'> Forgot Password</Link>
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </CardContent>
                        </Card>
                    </form>
                </Grid>
                <Grid item xs={2} md={3} lg={4}></Grid>
            </Grid>
        </Box>
    )
}
