import React, { useState, useEffect } from 'react'
import apiReq from '../scripts/apiReq'
import { LinearProgress, Box, Typography, Grid, TextField, MenuItem, Collapse, Alert, AlertTitle } from '@mui/material'
import moment from 'moment'
import SaveIcon from '@mui/icons-material/Save';
import AddLinkIcon from '@mui/icons-material/AddLink';
import LoadingButton from '@mui/lab/LoadingButton';


export default function DependantForm(props) {
    console.log({ props });
    const [dependantDetails, setDependantDetails] = useState(null);
    const [showStatus, setStatusMsg] = useState({ show: false, msg: '', title: '', severity: 'info' });
    const [customers, setClientList] = useState(false)
    let customerExisted = props.customerId !== undefined && props.customerId !== null;
    const [isSubmitting, setisSubmitting] = useState(false);

    useEffect(() => {
        if (dependantDetails === null && props.dependant !== null) {
            setDependantDetails(props.dependant);
            console.log('set', { props })
        }
    }, [dependantDetails, props]);

    useEffect(() => {
        if (customers === null) {
            let url = customerExisted ? '/customer/' + props.customerId : '/customers';
            apiReq.get(url)
                .then(data => {
                    let temp = customerExisted ? [data] : data;
                    setClientList(temp);
                })
                .catch(resp => setStatusMsg({ show: true, msg: 'Something went wrong updating', severity: 'error', title: 'Error' }));
        }
    }, [customers, customerExisted, props]);

    const handleChange = (e) => {
        // console.log('change', e.target.name, e.target.value);

        let value = e.target.name === 'dob' ? new Date(e.target.value).getTime() + 50000000 : e.target.value;
        setDependantDetails({
            ...dependantDetails,
            [e.target.name]: value
        })
    }

    if (dependantDetails === null) return <LinearProgress />

    const { gender, dob, name, description, id } = dependantDetails;

    const validateForm = () => {
        let proceed = true;
        let now = new Date().getTime();

        if (gender === "" || gender === null || gender === undefined) proceed = false;
        if (dob === "" || dob === null || dob === undefined) proceed = false;

        let dobMilli = new Date(dob).getTime();
        if (dobMilli > now) {
            setStatusMsg({ show: true, msg: 'Date of birth must be in the past', severity: 'error', title: 'Error' });
            proceed = false;
        }

        if (name === "" || name === null || name === undefined) proceed = false;

        if (!proceed) setisSubmitting(false);

        // console.log({proceed, startDateMilli ,endDateMilli})
        // return false;
        return proceed;
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        setisSubmitting(true);
        setStatusMsg({ show: false });

        let url = customerExisted ? '/customer/' + props.customerId + '/dependants' : '/customer/' + dependantDetails.Customer.id + '/dependants';
        // if(dependantDetails.id !== undefined) url += '/' + dependantDetails.id;
        if (!validateForm()) return;

        console.log("submitting", { url }, { dependantDetails });
        apiReq.post(url, dependantDetails).then(datazzzz => {
            setStatusMsg({ show: true, msg: 'Dependant updated', severity: 'success', title: 'Success' });
            setDependantDetails({
                ...dependantDetails,
                id: datazzzz.id
            })
            console.log({ datazzzz });
            setisSubmitting(false);
            props.refreshList();
        }).catch(resp => {
            console.log({ resp });
            let response = resp.response;
            let errMsg = response === undefined ? 'Something went wrong' : response.data.message;
            if (response.data.errors !== undefined && response.data.errors.length > 0) {
                errMsg = '';
                for (let msg in response.data.errors) errMsg += response.data.errors[msg] + '\n';
            }
            setStatusMsg({ show: true, msg: errMsg, severity: 'error', title: 'Error' });
            setisSubmitting(false);
        });
    }

    let saveBtnTxt = dependantDetails.id === undefined ? { txt: "Attach Dependant To Customer", icon: <AddLinkIcon /> } : { txt: "Save Dependant", icon: <SaveIcon /> };

    return (
        <Box component="form" autoComplete="off" onSubmit={handleSubmit}>
            <Grid container spacing={2} rowSpacing={2}>
                {
                    showStatus &&
                    <Grid item xs={12}>
                        <Collapse in={showStatus.show}>
                            <Alert severity={showStatus.severity} style={{ whiteSpace: 'pre-line' }}>
                                <AlertTitle>{showStatus.title}</AlertTitle>
                                {showStatus.msg}
                            </Alert>
                        </Collapse>
                    </Grid>
                }
                <Grid item xs={12}>
                    <Typography id="modal-modal-title" variant="h4" component="h2" align='center'>
                        {id !== undefined ? "Edit" : "New Dependant"}
                    </Typography>
                </Grid>
                <Grid item xs={12} md={12}>
                    <TextField fullWidth required id="outlined-required" name="name" label='Dependant Title' defaultValue={name} onChange={handleChange} />
                </Grid>
                <Grid item xs={12} md={6}>
                    <TextField fullWidth required helperText="Date of Birth" id="outlined-required" label="Choose dependant DOB" type="date" name='dob' onChange={handleChange} value={moment(dob).format("YYYY-MM-DD")} />
                </Grid>
                <Grid item xs={12} md={6}>
                    <TextField fullWidth select name='gender' label="Gender" value={gender} onChange={handleChange} helperText="Select the dependant gender" >
                        <MenuItem value="Male">Male</MenuItem>
                        <MenuItem value="Female">Female</MenuItem>
                    </TextField>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth id="outlined-required" label="Notes" name='description' defaultValue={description} onChange={handleChange} multiline rows={4} />
                </Grid>
                <Grid item xs={12} textAlign={'center'}>
                    <LoadingButton disabled={isSubmitting} loading={isSubmitting} variant="contained" type={'submit'} startIcon={saveBtnTxt.icon}>{saveBtnTxt.txt}</LoadingButton>
                </Grid>
            </Grid>
        </Box>
    )
}