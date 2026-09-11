import React from 'react';
import { createRoot } from 'react-dom/client';
import MySearch from '../components/MySearch/MySearch';
import { myData } from '../modelData/myData';

const container = document.getElementById('root');
const root = createRoot(container);
root.render(<MySearch model={myData} />);