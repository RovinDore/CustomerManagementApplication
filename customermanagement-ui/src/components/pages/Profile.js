import React, { useState, useEffect } from 'react'
import apiReq from '../scripts/apiReq'
import { Container, Box, Typography, Grid, TextField, Collapse, Alert, AlertTitle, LinearProgress, Checkbox } from '@mui/material'
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import LoadingButton from '@mui/lab/LoadingButton';
import SaveIcon from '@mui/icons-material/Save';
import Custom404 from './Custom404';

export default function Profile() {
    const [myUser, setMyUser] = useState(null);
    const [editPassword, setEditPassword] = useState(false);
    const [showStatus, setStatusMsg] = useState({ show: false, msg: '', title: '', severity: 'info' });
    const [isSubmitting, setisSubmitting] = useState(false);

    useEffect(() => {
        if (myUser === null) {
            let auth = localStorage.getItem("jwt");
            if (auth !== undefined && auth !== null) {
                auth = JSON.parse(auth);

                apiReq.get('/user/' + auth.user.id).then(data => {
                    data.password = '';
                    data.confirmpassword = '';
                    setMyUser(data);
                    // console.log({ data });
                }).catch(resp => {
                    console.log({ resp });
                    setMyUser(false);
                });
            }
        }
    }, [myUser]);

    if (myUser === null) return <LinearProgress />;
    if (!myUser) return <Custom404 />;

    const { username, email, password, id, confirmpassword } = myUser;

    const validateForm = () => {
        if (email === "" || email === null || email === undefined) return false;
        if (username === "" || username === null || username === undefined) return false;

        if(editPassword){
            if (password === "" || password === null || password === undefined) return false;
            if (confirmpassword === "" || confirmpassword === null || confirmpassword === undefined) return false;
            if (password !== confirmpassword) {
                setStatusMsg({ show: true, msg: 'Passwords does not match!', severity: 'error', title: 'Error' });
                return false;
            }
        }
        

        return true;
    }

    const handleSubmit = (e) => {
        setisSubmitting(true);
        setStatusMsg({ show: false });
        e.preventDefault();
        let url = '/user/' + id;

        if (!validateForm()) {
            setisSubmitting(false);
            return
        }

        delete myUser.file;
        console.log({ myUser });
        let dataToSend = myUser;
        dataToSend.userName = username;
        if(!editPassword) dataToSend.password = '';
        apiReq.post(url, dataToSend).then(datazzzz => {
            console.log({ datazzzz });
            setStatusMsg({ show: true, msg: 'Profile updated', severity: 'success', title: 'Success' });
            setisSubmitting(false);
            // setMyUser(null);
        }).catch(resp => {
            console.log({ resp });
            setStatusMsg({ show: true, msg: 'Something went wrong updating', severity: 'error', title: 'Error' });
            setisSubmitting(false);
        });
    }

    const handleChange = (e) => {
        // console.log('change', e.target.name, e.target.value);
        setMyUser({ ...myUser, [e.target.name]: e.target.value.trim() })
    }

    const togglePassword = () => {
        const state = editPassword ? false : true;
        setEditPassword(state);
    }

    return (
        <div className="App">
            <Container>
                <Box paddingTop={2}>
                    <Grid container rowSpacing={2}>
                        <Grid item xs={3}></Grid>
                        <Grid item xs={12} md={6}>
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
                                                <Typography variant='h5' align='center'>My Profile</Typography>
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
                                                        <Checkbox type="checkbox" name='editPassword' id='editPassword' defaultValue={editPassword} onChange={togglePassword} />
                                                        <label htmlFor='editPassword'>Edit Password</label>
                                                    </Grid>
                                                    {
                                                        editPassword &&
                                                        <Grid container rowSpacing={2} item xs={12}>
                                                            <Grid item xs={12}>
                                                                <TextField type={'password'} required fullWidth name="password" label='Password' defaultValue={password} onChange={handleChange} />
                                                            </Grid>
                                                            <Grid item xs={12}>
                                                                <TextField type={'password'} required fullWidth name="confirmpassword" label='Confirm Password' defaultValue={confirmpassword} onChange={handleChange} />
                                                            </Grid>
                                                        </Grid>
                                                    }
                                                </Grid>
                                            </Grid>
                                        </Grid>
                                    </CardContent>
                                    <CardActions style={{ justifyContent: 'center' }}>
                                        <LoadingButton loading={isSubmitting} disabled={isSubmitting} color='success' variant="contained" type='submit' startIcon={<SaveIcon />}>Update Profile</LoadingButton>
                                    </CardActions>
                                </Card>
                            </form>
                        </Grid>
                        <Grid item xs={3}></Grid>
                    </Grid>
                </Box>
            </Container>
        </div>
    )
}
