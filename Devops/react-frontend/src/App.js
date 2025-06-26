import React, { useEffect, useState } from 'react';
import { AppBar, Toolbar, Typography, Container, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, Box, Chip, Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'
import ListEmployeeComponent from './components/ListEmployeeComponent';
import HeaderComponent from './components/HeaderComponent';
import FooterComponent from './components/FooterComponent';
import CreateEmployeeComponent from './components/CreateEmployeeComponent';
import UpdateEmployeeComponent from './components/UpdateEmployeeComponent';
import ViewEmployeeComponent from './components/ViewEmployeeComponent';
import JenkinsDashboardComponent from './components/JenkinsDashboardComponent';

const theme = createTheme();

function App() {
  return (
    <JenkinsDashboardComponent />
  );
}

export default App;
