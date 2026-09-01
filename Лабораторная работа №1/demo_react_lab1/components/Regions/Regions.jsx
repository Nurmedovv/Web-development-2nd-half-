import React, { useState, useMemo } from 'react';

function Regions(props) {
    const [substring, setSubstring] = useState('');
    const regions = props.model || [];

    const filteredRegions = useMemo(() => {
        if (!substring.trim()) {
            return [];
        }
        const lowerSubstring = substring.toLowerCase();
        return regions
            .filter(region => region.toLowerCase().includes(lowerSubstring))
            .sort();
    }, [regions, substring]);

    function handleInputChange(event) {
        setSubstring(event.target.value);
    }

    return (
        <div>
            <h2>Поиск областей Беларуси</h2>
            <div>
                <label>Введите подстроку: </label>
                <input type="text" value={substring} onChange={handleInputChange} />
            </div>
            <p>Искомая подстрока: {substring || "(пусто)"}</p>
            {filteredRegions.length > 0 ? (
                <ul>
                    {filteredRegions.map((region, index) => (
                        <li key={index}>{region}</li>
                    ))}
                </ul>
            ) : (
                substring.trim() && <p>Совпадений не найдено</p>
            )}
        </div>
    );
}

export default Regions;