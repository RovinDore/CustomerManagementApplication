import React, { useState } from 'react'
import { TextField, Grid, Card, CardContent, Typography, Collapse, Alert, AlertTitle, Box } from '@mui/material'
import SendIcon from '@mui/icons-material/Send';
import apiReq from '../scripts/apiReq'
import User from '../../assets/forgotPassword.png'
import LoadingButton from '@mui/lab/LoadingButton';
import { Link } from 'react-router-dom'

export default function ForgetPassword(props) {
    const [requestCredentials, setRequestCredentials] = useState({ email: '' });
    const [showStatus, setStatusMsg] = useState({ show: false, msg: '', title: '', severity: 'info' });
    const [isSubmitting, setisSubmitting] = useState(false);

    const handleChange = (e) => {
        // console.log('change', e.target.name, e.target.value);

        let value = e.target.value;
        setRequestCredentials({
            ...requestCredentials,
            [e.target.name]: value
        })
    }

    let { email } = requestCredentials;

    const validateForm = () => {
        if (email === "" || email === null || email === undefined) return false;

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
            setStatusMsg({ show: true, msg: 'Enter your email', severity: 'error' });
            return;
        }

        // send login request
        apiReq.post('/auth/forgot', requestCredentials).then(access => {
            console.log({ access });
            setStatusMsg({ show: false });
            // check if login was a sucess
            if(access.error){
                setStatusMsg({ show: true, msg: access.message, severity: 'error' });
                setisSubmitting(false);
            } else{
                setRequestCredentials({email: ''});
                setStatusMsg({ show: true, msg: 'Please check your email for instructions', severity: 'success' });
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
                                            Send Request
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <TextField required type={'email'} name="email" label='Email' value={email} onChange={handleChange} />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <LoadingButton loading={isSubmitting} variant="contained" type={'submit'} startIcon={<SendIcon />}>Send Link</LoadingButton>
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
