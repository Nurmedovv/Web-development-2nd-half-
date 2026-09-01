import React, { useState, useMemo } from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';

function MySearch(props) {
    const [substring, setSubstring] = useState('');
    const data = props.model || [];

    const filteredData = useMemo(() => {
        if (!substring.trim()) {
            return [];
        }
        const lowerSubstring = substring.toLowerCase();
        return data
            .filter(item => item.toLowerCase().includes(lowerSubstring))
            .sort();
    }, [data, substring]);

    function handleInputChange(event) {
        setSubstring(event.target.value);
    }

    return (
        <Paper elevation={3} style={{ padding: '20px', maxWidth: '600px', margin: '20px auto' }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Автобаза. Список автомобилей
            </Typography>
            <TextField
                fullWidth
                label="Введите подстроку для поиска"
                variant="outlined"
                value={substring}
                onChange={handleInputChange}
                margin="normal"
            />
            <Typography variant="body1" gutterBottom>
                Искомая подстрока: {substring || "(пусто)"}
            </Typography>
            {filteredData.length > 0 ? (
                <List component="nav" aria-label="список автомобилей">
                    {filteredData.map((item, index) => (
                        <ListItem key={index} divider>
                            <ListItemText primary={item} />
                        </ListItem>
                    ))}
                </List>
            ) : (
                substring.trim() && (
                    <Typography color="textSecondary" style={{ marginTop: '16px' }}>
                        Совпадений не найдено
                    </Typography>
                )
            )}
        </Paper>
    );
}

export default MySearch;