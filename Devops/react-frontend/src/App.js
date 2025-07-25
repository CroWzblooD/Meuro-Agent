import React, { useEffect, useState } from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';

import JenkinsDashboardComponent from './components/JenkinsDashboardComponent';

const theme = createTheme();

function App() {
  return (
    <JenkinsDashboardComponent />
  );
}

export default App;
