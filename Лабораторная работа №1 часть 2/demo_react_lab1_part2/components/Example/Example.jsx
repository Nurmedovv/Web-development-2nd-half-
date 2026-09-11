import React, { useState, useEffect } from 'react';
import './Example.css';

function Example(props) {
    const [name, setName] = useState(props.model.name || "Unknown name");

    useEffect(() => {
        setName(props.model.name || "Unknown name");
    }, [props.model.name]);

    function handleChange(event) {
        setName(event.target.value);
    }

    return (
        <div>
            <h1>Getting started page</h1>
            <div>
                <label>Name: </label>
                <input type="text" value={name} onChange={handleChange} />
            </div>
            <h2>Hello {name}!</h2>
        </div>
    );
}

export default Example;