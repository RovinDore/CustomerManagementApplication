import React, { useEffect, useState } from 'react'
import apiReq from '../scripts/apiReq'
import { Container, Box, Typography, Grid, Button, TextField, LinearProgress, Modal, Collapse, Alert, AlertTitle, Tooltip } from '@mui/material'
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import PageviewIcon from '@mui/icons-material/Edit';
import { Link } from 'react-router-dom'
import AddCircleIcon from '@mui/icons-material/AddCircle';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import DeleteIcon from '@mui/icons-material/Delete';
import FileUploadIcon from '@mui/icons-material/FileUpload';
import ConfirmationModal from '../misc/ConfirmationModal'

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};

const blankClient = {
    name: "",
    phoneNumber: "",
    email: "",
    file: ''
}

function ClientForm(props) {
    const [clientDetails, setClientDetails] = useState(null);
    const [showStatus, setStatusMsg] = useState({ show: false, msg: '', title: '', severity: 'success' });
    const [fileUploaded, setFileUploaded] = useState(false);
    const [disabled, setdisabled] = useState(false);

    useEffect(() => {
        if (clientDetails === null && props.project !== null) {
            setClientDetails(props.client);
            // console.log({ props })
        }
    }, [clientDetails, props]);

    useEffect(() => {
        if (fileUploaded) {
            apiReq.post('/customer', clientDetails).then(datazzzz => {
                setStatusMsg({ show: true, msg: 'Customer updated!', severity: 'success', title: 'Success' });
                setFileUploaded(false);
                setdisabled(false);
                props.refreshList();
                // console.log({ datazzzz });
            }).catch(resp => {
                console.log({ resp });
                let response = resp.response;
                let errMsg = response === undefined ? 'Something went wrong' : response.data.message;
                if(response.data.errors !== undefined && response.data.errors.length > 0){
                    errMsg = '';
                    for(let msg in response.data.errors) errMsg += response.data.errors[msg] + '\n';
                }
                
                setStatusMsg({ show: true, msg: errMsg, severity: 'error', title: 'Error' });
                setdisabled(false);
            });
        }
    }, [fileUploaded, clientDetails, props])

    if (clientDetails === null) return <LinearProgress />

    const { phoneNumber, email, name, file } = clientDetails;

    const handleChange = (e) => {
        let value = e.target.name === 'file' ? e.target.files[0] : e.target.value.trim();
        console.log({ value }, e.target.name)
        setClientDetails({
            ...clientDetails,
            [e.target.name]: value
        });
    }

    const validateForm = () => {
        let proceed = true;
        if (phoneNumber === "" || phoneNumber === null || phoneNumber === undefined) proceed = false;
        if (email === "" || email === null || email === undefined) proceed = false;
        if (name === "" || name === null || name === undefined) proceed = false;
        if (file === "" || file === null || file === undefined) {
            setStatusMsg({ show: true, msg: 'Please upload the Identification file', severity: 'info', title: 'Missing file' });
            proceed = false;
        }

        if (!proceed) setdisabled(false);

        return proceed;
    }

    const handleSubmit = (e) => {
        setdisabled(true);
        setStatusMsg({ show: false });
        e.preventDefault();
        console.log({ clientDetails });
        if (!validateForm()) return;

        let tmp = { name, email, phoneNumber };

        apiReq.post('/customerExists', tmp).then(data => {
            if (data.error === undefined) {
                console.log({ data });
                // setdisabled(false);
                if (!data) {
                    let formData = new FormData();
                    formData.append('file', file);

                    apiReq.uploadFile('/uploadFile', formData).then(resp => {
                        console.log({ resp });
                        if (resp === false || resp === null || resp === '') {
                            setStatusMsg({ show: true, msg: 'Something went wrong while uploading file', severity: 'error', title: 'Error' });
                            setdisabled(false);
                            return;
                        }
                        setClientDetails({
                            ...clientDetails,
                            file: resp
                        });

                        setFileUploaded(true);
                    }).catch(rsp => {
                        console.log({ rsp })
                        setStatusMsg({ show: true, msg: 'Something went wrong while uploading file\n Please make sure you are uploading a PDF / JPG file.', severity: 'error', title: 'Error' });
                        setdisabled(false);
                    });
                } else { setStatusMsg({ show: true, msg: 'This email is already being used!', severity: 'error', title: 'Duplicate Entry' }); setdisabled(false); };
            }

        }).catch(rsp => { setStatusMsg({ show: true, msg: 'Something went wrong', severity: 'error', title: 'Error' }); setdisabled(false); })


    }

    return (
        <form autoComplete="off" onSubmit={handleSubmit} encType="multipart/form-data">
            <Grid container spacing={2} alignItems="center" justifyContent="center">
                {
                    showStatus.show &&
                    <Grid item xs={12}>
                        <Collapse in={showStatus.show}>
                            <Alert severity={showStatus.severity} style={{whiteSpace: 'pre-line'}}>
                                <AlertTitle>{showStatus.title}</AlertTitle>
                                {showStatus.msg}
                            </Alert>
                        </Collapse>
                    </Grid>
                }
                <Grid item xs={12}>
                    <Typography id="modal-modal-title" variant="h6" component="h2" align='center'>
                        New Customer
                    </Typography>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth required label="Name" name='name' defaultValue={name} onChange={handleChange} />
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth type='number' required name="phoneNumber" label='Phone Number' defaultValue={phoneNumber} onChange={handleChange} />
                </Grid>
                <Grid item xs={6}>
                    <TextField fullWidth required label="Email" name='email' defaultValue={email} onChange={handleChange} />
                </Grid>
                <Grid item xs={6} justifyContent='center'>
                    {/* <TextField type='file' fullWidth required label="Agreement File" name='file' onChange={handleChange} /> */}
                    <Button variant="contained" component="label" startIcon={<FileUploadIcon />}>
                        Identification
                        <input type="file" name="file" onChange={handleChange} accept='application/pdf, image/*' hidden />
                    </Button>
                </Grid>
                <Grid item xs={12} textAlign={'center'}>
                    <Button disabled={disabled} variant="contained" color='success' type="submit" startIcon={<PersonAddIcon />}>Add Customer</Button>
                </Grid>
            </Grid>
        </form>
    )
}

export default function Home() {
    const [clients, setClients] = useState(null);
    const [clientInView, setClientInView] = useState(null);
    const [modalState, setmodalState] = useState(false);
    const [modalContent, setModalContent] = useState(null);
    const [showStatus, setStatusMsg] = useState({ show: false, msg: '', title: '', severity: 'success' });

    useEffect(() => {
        if (clients === null) {
            apiReq.get('/customers').then(data => {
                setClients(data);
                setStatusMsg({ show: false });

                console.log({ data });
            }).catch(resp => {
                console.log({ resp });
                setStatusMsg({ show: true, msg: 'Something went wrong while retrieving data', severity: 'error', title: 'Error' });
                setClients([]);
            });
        }
    }, [clients])

    useEffect(() => {
        if (!modalState) setClientInView(null);
    }, [modalState]);

    const handleOpenModal = (client, type) => {
        setModalContent(type);
        setClientInView(client);
        setmodalState(true);
    }

    const handleCloseModal = () => {
        setmodalState(false);
    }

    const deleteClient = (id, nextId) => {
        console.log({ id, nextId })
        let idToBeDeleted = id === undefined || id === 0 ? nextId : id;
        apiReq.delete('/customer/' + idToBeDeleted).then(data => {
            // console.log({data});
            if (data.Deleted) {
                handleCloseModal();
                setClients(null);
            }
            // setmodalState(false);
        }).catch(resp => {
            console.log({ resp });
            setStatusMsg({ show: true, msg: 'Something went wrong deleting customer', severity: 'error', title: 'Error' });
        });
    }

    return (
        <div>
            <Modal open={modalState} onClose={handleCloseModal} aria-labelledby="modal-modal-title">
                <Box sx={style}>
                    {
                        modalContent === 'client' &&
                        <ClientForm client={clientInView} refreshList={() => { handleCloseModal(); setClients(null); }} />
                    }
                    {
                        modalContent === 'confirmation' &&
                        <ConfirmationModal type='customer' data={clientInView} deleteClient={deleteClient} handleCloseModal={handleCloseModal} />
                    }
                </Box>
            </Modal>
            <div className="App">
                <Container>
                    <Box paddingTop={2}>
                        <Grid container rowSpacing={2}>
                            <Grid item xs={12}>
                                <Collapse in={showStatus.show}>
                                    <Alert severity={showStatus.severity} >
                                        <AlertTitle>{showStatus.title}</AlertTitle>
                                        {showStatus.msg}
                                    </Alert>
                                </Collapse>
                            </Grid>
                            <Grid item xs={12}>
                                <Typography variant='h3' component={'h3'} align='center' color={'#fff'}>Customers</Typography>
                            </Grid>
                            <Grid item xs={12}>
                                <TableContainer component={Paper}>
                                    <Table sx={{ minWidth: 650 }} aria-label="simple table">
                                        <TableHead>
                                            <TableRow>
                                                <TableCell sx={{ fontWeight: 'bold' }}>Name</TableCell>
                                                <TableCell sx={{ fontWeight: 'bold' }} align="right">Email</TableCell>
                                                <TableCell sx={{ fontWeight: 'bold' }} align="right">Phone Number</TableCell>
                                                <TableCell sx={{ fontWeight: 'bold' }} align="right">Dependants</TableCell>
                                                <TableCell sx={{ fontWeight: 'bold' }} align="right">Actions</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {
                                                clients !== null && clients.length > 0 ?
                                                    clients.map((data, index) => {
                                                        const { name, phoneNumber, email, id, dependantAmount } = data;
                                                        return (
                                                            <TableRow key={index} sx={{ '&:last-child td, &:last-child th': { border: 0 } }} >
                                                                <TableCell component="th" scope="row">
                                                                    <Typography variant='span'>{name}</Typography>
                                                                </TableCell>
                                                                <TableCell align="right">
                                                                    <Typography variant='span'>{email}</Typography>
                                                                </TableCell>
                                                                <TableCell align="right">
                                                                    <Typography variant='span'>{phoneNumber}</Typography>
                                                                </TableCell>
                                                                <TableCell align="right">
                                                                    <Typography variant='span'>{dependantAmount}</Typography>
                                                                </TableCell>
                                                                <TableCell align="right">
                                                                    <Tooltip title={'Edit Customer'}>
                                                                        <Button component={Link} to={"/customer/" + id} variant="text" color='success' startIcon={<PageviewIcon />}></Button>
                                                                    </Tooltip>
                                                                    <Tooltip title={'Delete Customer'}>
                                                                        <Button onClick={() => handleOpenModal(data, 'confirmation')} variant="text" color='error' startIcon={<DeleteIcon />}></Button>
                                                                    </Tooltip>
                                                                </TableCell>
                                                            </TableRow>
                                                        )
                                                    })
                                                    :
                                                    <TableRow sx={{ '&:last-child td, &:last-child th': { border: 0 } }} >
                                                        <TableCell align='center' colSpan={5}>
                                                            <Typography variant='span'>No Customers</Typography>
                                                        </TableCell>
                                                    </TableRow>
                                            }
                                            <TableRow sx={{ '&:last-child td, &:last-child th': { border: 0 } }} >
                                                <TableCell component="th" scope="row" colSpan={4}>
                                                    <Typography align="right">Add new Customer</Typography>
                                                </TableCell>
                                                <TableCell align="right" colSpan={5}>
                                                    <Tooltip title='Add New Customer'>
                                                        <Button onClick={() => handleOpenModal(blankClient, 'client')} variant="text" startIcon={<AddCircleIcon />}></Button>
                                                    </Tooltip>
                                                </TableCell>
                                            </TableRow>
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            </Grid>
                        </Grid>
                    </Box>
                </Container>
            </div>
        </div>
    )
}
