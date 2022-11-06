import React, { useState, useEffect } from 'react'
import { useParams } from "react-router-dom";
import apiReq from '../scripts/apiReq'
import { LinearProgress, Container, Box, Typography, Grid, Button, TextField, Modal, Collapse, Alert, AlertTitle, Tooltip } from '@mui/material'
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import PageviewIcon from '@mui/icons-material/Edit';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import SaveIcon from '@mui/icons-material/Save';
import VisibilityIcon from '@mui/icons-material/Visibility';
import DeleteIcon from '@mui/icons-material/Delete';
import ProjectForm from '../misc/DependantForm';
import ConfirmationModal from '../misc/ConfirmationModal'
import LoadingButton from '@mui/lab/LoadingButton';
import moment from 'moment';

const blankDependant = {
    dob: new Date(),
    gender: "Male",
    name: "",
    description: "",
    newProject: true,
    // id: 0
}

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 600,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};


export default function Customer(props) {
    const [client, setClient] = useState(null);
    // const [viewMode, setviewMode] = useState('view');
    const [dependants, setProjects] = useState([]);
    const [projectInView, setProjectInView] = useState(null);
    let { customerId } = useParams();
    if (props.customerId !== undefined) customerId = props.customerId;

    const [modalState, setmodalState] = useState(false);
    const [showStatus, setStatusMsg] = useState({ show: false, msg: '', title: '', severity: 'info' });
    const [modalContent, setModalContent] = useState(null);
    const [isSubmitting, setisSubmitting] = useState(false);

    useEffect(() => {
        if (client === null) {
            console.log({ customerId })
            apiReq.get('/customer/' + customerId).then(data => {
                setClient(data);
                // console.log({ data });
            }).catch(resp => {
                console.log({ resp });
            });
        }
    }, [client, customerId]);

    useEffect(() => {
        if (dependants.length === 0) {
            console.log('hiii');
            apiReq.get('/customer/' + customerId + '/dependants').then(dependantList => {
                if (dependantList.length !== 0) setProjects(dependantList);
                // console.log({ dependantList });
            }).catch(resp => {
                console.log({ resp });
            });
        }
    }, [dependants, customerId]);

    useEffect(() => {
        if (!modalState) setProjectInView(null);
    }, [modalState]);

    const handleOpenModal = (dependant, type) => {
        setModalContent(type);
        setProjectInView(dependant);
        setmodalState(true);
    }

    const handleCloseModal = () => {
        setmodalState(false);
    }

    // console.log({props, id});
    if (client === null) return <LinearProgress />

    //eslint-disable-next-line
    const { name, email, phoneNumber, id, bio, file } = client;

    const handleChange = (e) => {
        
        let value = e.target.value.trim();
        if(e.target.name === 'phoneNumber') value = value.replace(/\D/g,'');
        console.log('change', value);
        setClient({ ...client, [e.target.name]: value });
    }

    const validateForm = () => {
        // if(phoneNumber === "" || phoneNumber === null || phoneNumber === undefined ) return false;
        if (email === "" || email === null || email === undefined) return false;
        if (name === "" || name === null || name === undefined) return false;

        return true;
    }

    const handleSubmit = (e) => {
        setisSubmitting(true);
        setStatusMsg({ show: false });
        e.preventDefault();
        let url = '/customer';

        if (!validateForm()) {
            setisSubmitting(false);
            return
        }

        delete client.file;
        console.log({ client })
        apiReq.post(url, client).then(datazzzz => {
            console.log({ datazzzz });
            setStatusMsg({ show: true, msg: 'Customer updated', severity: 'success', title: 'Success' });
            setisSubmitting(false);
            // setClient(null);
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

    const deleteProject = (dependantId) => {
        apiReq.delete('/dependants/' + dependantId).then(data => {
            // console.log({ data });
            if (data.Deleted) {
                handleCloseModal();
                setProjects([]);
            }
            // setmodalState(false);
        }).catch(resp => {
            console.log({ resp });
        });
    }

    return (
        <div>
            <Modal open={modalState} onClose={handleCloseModal} aria-labelledby="modal-modal-title">
                <Grid container spacing={2} sx={style}>
                    <Grid item xs={12}>
                        {
                            modalContent === 'confirmation' &&
                            <ConfirmationModal type='dependant' data={projectInView} customerId={id} deleteClient={deleteProject} handleCloseModal={handleCloseModal} />
                        }
                        {
                            modalContent === 'dependant' &&
                            <ProjectForm dependant={projectInView} customerId={id} refreshList={() => setProjects([])} />
                        }
                    </Grid>
                </Grid>
            </Modal>
            <div className="App">
                <Container>
                    <Box paddingTop={2}>
                        <Grid container rowSpacing={2}>
                            <Grid item xs={12}>
                                <Typography variant='h3' component={'h3'} align='center' color={'#fff'}>Customer Details</Typography>
                            </Grid>
                            <Grid item xs={12}>
                                <form autoComplete="off" onSubmit={handleSubmit}>
                                    <Card>
                                        <CardContent>
                                            <Grid container spacing={2}>
                                                <Grid item xs={12}>
                                                    <Collapse in={showStatus.show}>
                                                        <Alert severity={showStatus.severity} style={{ whiteSpace: 'pre-line' }}>
                                                            <AlertTitle>{showStatus.title}</AlertTitle>
                                                            {showStatus.msg}
                                                        </Alert>
                                                    </Collapse>
                                                </Grid>
                                                <Grid item xs={12}>
                                                    <Typography variant='h5' align='center'>Details</Typography>
                                                </Grid>
                                                <Grid container item>
                                                    <Grid container item xs={12} md={6} rowSpacing={2} style={{ paddingRight: 20 }}>
                                                        <Grid item xs={12}>
                                                            <TextField required fullWidth id="outlined-required" label="Name" name='name' defaultValue={name} onChange={handleChange} />
                                                        </Grid>
                                                        <Grid item xs={12}>
                                                            <TextField required fullWidth id="outlined-required" type="email" label="Email" name='email' defaultValue={email} onChange={handleChange} />
                                                        </Grid>
                                                        <Grid item xs={12}>
                                                            <TextField required fullWidth id="outlined-required" name="phoneNumber" label='Phone Number' value={phoneNumber} onChange={handleChange} />
                                                        </Grid>
                                                    </Grid>
                                                    <Grid item xs={12} md={6} container justifyContent={'center'}>
                                                        <Grid item xs={12}>
                                                            <TextField label="Notes" fullWidth name='bio' defaultValue={bio} onChange={handleChange} multiline rows={4} />
                                                        </Grid>
                                                        {
                                                            file !== null && file !== undefined &&
                                                            <Grid item xs={12} style={{ marginTop: 20 }}>
                                                                <Button endIcon={<VisibilityIcon />} variant='contained' color='info' onClick={() => window.open('http://localhost:8080/api/v1/files/' + file, '_blank')}>Identification</Button>
                                                            </Grid>
                                                        }
                                                    </Grid>
                                                </Grid>
                                            </Grid>
                                        </CardContent>
                                        <CardActions>
                                            <LoadingButton loading={isSubmitting} disabled={isSubmitting} color='success' variant="contained" type='submit' startIcon={<SaveIcon />}>Update Customer</LoadingButton>
                                        </CardActions>
                                    </Card>
                                </form>
                            </Grid>
                        </Grid>
                    </Box>
                    <Box paddingTop={4}>
                        <Grid container rowSpacing={2}>
                            <Grid item xs={12}>
                                <Typography variant='h3' component={'h3'} align='center' color={'#fff'}>Dependants</Typography>
                            </Grid>
                            <Grid item xs={12}>
                                <TableContainer component={Paper}>
                                    <Table sx={{ minWidth: 650 }} aria-label="simple table">
                                        <TableHead>
                                            <TableRow>
                                                <TableCell sx={{ fontWeight: 'bold' }}>Name</TableCell>
                                                <TableCell sx={{ fontWeight: 'bold' }} align="right">Age</TableCell>
                                                <TableCell sx={{ fontWeight: 'bold' }} align="right">Gender</TableCell>
                                                <TableCell sx={{ fontWeight: 'bold' }} align="right"></TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {
                                                dependants.length > 0 ?
                                                    dependants.map((data, index) => {
                                                        //eslint-disable-next-line
                                                        const { name, gender, dob, status } = data;
                                                        return (
                                                            <TableRow key={index} sx={{ '&:last-child td, &:last-child th': { border: 0 } }} >
                                                                <TableCell component="th" scope="row">
                                                                    <Typography variant='span'>{name}</Typography>
                                                                </TableCell>
                                                                <TableCell align="right">
                                                                    <Typography variant='span'>{moment(dob).fromNow(true)}</Typography>
                                                                </TableCell>
                                                                <TableCell align="right">
                                                                    <Typography variant='span'>{gender}</Typography>
                                                                </TableCell>
                                                                <TableCell align="right">
                                                                    <Tooltip title={'Edit Dependant'}>
                                                                        <Button onClick={() => handleOpenModal(data, 'dependant')} variant="text" startIcon={<PageviewIcon />}></Button>
                                                                    </Tooltip>
                                                                    <Tooltip title={'Delete Dependant'}>
                                                                        <Button color="error" onClick={() => handleOpenModal(data, 'confirmation')} variant="text" startIcon={<DeleteIcon />}></Button>
                                                                    </Tooltip>
                                                                </TableCell>
                                                            </TableRow>
                                                        )
                                                    })
                                                    :
                                                    <TableRow sx={{ '&:last-child td, &:last-child th': { border: 0 } }} >
                                                        <TableCell align="center" colSpan={4}>
                                                            <Typography variant='span' component='p' align='center'>No dependants Assigned</Typography>
                                                        </TableCell>
                                                    </TableRow>
                                            }
                                            <TableRow sx={{ '&:last-child td, &:last-child th': { border: 0 } }} >
                                                <TableCell component="th" scope="row" colSpan={3}>
                                                    <Typography align="right">Add new dependant</Typography>
                                                </TableCell>
                                                <TableCell align="right">
                                                    <Tooltip title={'Add Dependant'}>
                                                        <Button onClick={() => handleOpenModal(blankDependant, 'dependant')} variant="text" startIcon={<AddCircleIcon />}></Button>
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
