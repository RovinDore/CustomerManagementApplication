import React, { useState } from 'react'
import { TextField, Grid, Card, CardContent, Typography, Collapse, Alert, AlertTitle, Box } from '@mui/material'
import PasswordIcon from '@mui/icons-material/Password';
import apiReq from '../scripts/apiReq'
import User from '../../assets/resetPassword.png'
import LoadingButton from '@mui/lab/LoadingButton';
import { Link, useParams } from 'react-router-dom'

export default function ResetPassword(props) {
    const [requestCredentials, setRequestCredentials] = useState({ password: '', confirmPassword: '' });
    const [showStatus, setStatusMsg] = useState({ show: false, msg: '', title: '', severity: 'info' });
    const [isSubmitting, setisSubmitting] = useState(false);
    let { userId, keyCode } = useParams();

    const handleChange = (e) => {
        // console.log('change', e.target.name, e.target.value);

        let value = e.target.value;
        setRequestCredentials({
            ...requestCredentials,
            [e.target.name]: value
        })
    }

    let { password, confirmPassword } = requestCredentials;

    const validateForm = () => {
        if (password === "" || password === null || password === undefined) return false;
        if (confirmPassword === "" || confirmPassword === null || confirmPassword === undefined) return false;
        if (confirmPassword !== password) return false;
        if(password.length < 7) return false;

        return true;
    }

    const handleSubmit = (e) => {
        // console.log('submiotting')
        setisSubmitting(true);
        setStatusMsg({ show: false });
        e.preventDefault();

        // validate form
        if (!validateForm()) {
            setisSubmitting(false);
            setStatusMsg({ show: true, msg: 'Please enter a valid password', severity: 'error' });
            return;
        }

        let dataToSend = { userId, password: requestCredentials.password, key: keyCode }

        // send login request
        apiReq.post('/auth/reset', dataToSend).then(access => {
            console.log({ access });
            setStatusMsg({ show: false });
            // check if login was a sucess
            if(access.error){
                setStatusMsg({ show: true, msg: access.message, severity: 'error' });
                setisSubmitting(false);
            } else{
                setRequestCredentials({password: ''});
                setStatusMsg({ show: true, msg: 'Password updated! Return to login', severity: 'success' });
                setisSubmitting(false);
            }
        }).catch(rsp => {
            console.log({rsp});
            // something went wrong with API / connection
            let response = rsp.response;
            let errMsg = rsp.response === undefined ? 'Something went wrong' : response.data.message;
            setStatusMsg({ show: true, msg: errMsg, severity: 'error' });
            setisSubmitting(false);
        })
    }

    return (
        <Box sx={{ backgroundColor: 'darkgrey', minHeight: '102vh', paddingBottom: 0 }}>
            <Grid container spacing={2} style={{ paddingTop: '5%' }}>
                <Grid item xs={2} md={3} lg={4}></Grid>
                <Grid item xs={8} md={6} lg={4} textAlign='center'>
                    <Card>
                        <CardContent>
                            <Box component="form" autoComplete="off" onSubmit={handleSubmit}>
                                <Grid container spacing={2} rowSpacing={2}>
                                    <Grid item xs={12}>
                                        <Collapse in={showStatus.show}>
                                            <Alert severity={showStatus.severity} >
                                                <AlertTitle>{showStatus.title}</AlertTitle>
                                                {showStatus.msg}
                                            </Alert>
                                        </Collapse>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <img src={User} alt='userlogo' style={{ height: 125 }} />
                                        <Typography gutterBottom variant="h4" component="h2" align='center'>
                                            Reset password
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField required type={'password'} name="password" label='Password' value={password} onChange={handleChange} />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField required type={'password'} name="confirmPassword" label='Confirm Password' value={confirmPassword} onChange={handleChange} />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <LoadingButton loading={isSubmitting} variant="contained" type={'submit'} startIcon={<PasswordIcon />}>Reset password</LoadingButton>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <Link to='/'>Back to Login</Link>
                                    </Grid>
                                </Grid>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={2} lg={4} md={3}></Grid>
            </Grid>
        </Box>
    )
}
