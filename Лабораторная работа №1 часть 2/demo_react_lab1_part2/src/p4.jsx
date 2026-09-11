import React from 'react';
import { createRoot } from 'react-dom/client';
import { HashRouter, Routes, Route, Link } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Box, Grid, List, ListItem, ListItemText, Paper } from '@mui/material';

import Example from '../components/Example/Example';
import Regions from '../components/Regions/Regions';
import MySearch from '../components/MySearch/MySearch';

import { exampleData } from '../modelData/example';
import { regionsData } from '../modelData/regions';
import { myData } from '../modelData/myData';

function Home() {
    return (
        <Paper elevation={2} style={{ padding: '20px', margin: '20px' }}>
            <Typography variant="h4" gutterBottom>
                Добро пожаловать!
            </Typography>
            <Typography variant="body1">
                Выберите компонент в меню слева.
            </Typography>
        </Paper>
    );
}

function App() {
    return (
        <HashRouter>
            <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
                <AppBar position="static" color="primary">
                    <Toolbar>
                        <Typography variant="h6" sx={{ flexGrow: 1 }}>
                            🚗 Лабораторная работа №1, часть 2.
                        </Typography>
                    </Toolbar>
                </AppBar>

                <Box sx={{ display: 'flex', flexGrow: 1 }}>
                    <Grid container sx={{ height: '100%' }}>
                        <Grid item xs={12} md={3} sx={{ bgcolor: '#f5f5f5', p: 2 }}>
                            <Typography variant="h6" gutterBottom>
                                📂 Меню
                            </Typography>
                            <List component="nav">
                                <ListItem button component={Link} to="/">
                                    <ListItemText primary="🏠 Главная" />
                                </ListItem>
                                <ListItem button component={Link} to="/example">
                                    <ListItemText primary="📝 Getting Started" />
                                </ListItem>
                                <ListItem button component={Link} to="/regions">
                                    <ListItemText primary="🗺️ Регионы" />
                                </ListItem>
                                <ListItem button component={Link} to="/mysearch">
                                    <ListItemText primary="🚗 Автобаза" />
                                </ListItem>
                            </List>
                        </Grid>

                        <Grid item xs={12} md={9} sx={{ p: 2 }}>
                            <Routes>
                                <Route path="/" element={<Home />} />
                                <Route path="/example" element={<Example model={exampleData} />} />
                                <Route path="/regions" element={<Regions model={regionsData} />} />
                                <Route path="/mysearch" element={<MySearch model={myData} />} />
                            </Routes>
                        </Grid>
                    </Grid>
                </Box>
            </Box>
        </HashRouter>
    );
}

const container = document.getElementById('root');
const root = createRoot(container);
root.render(<App />);