import React from 'react';
import { createRoot } from 'react-dom/client';
import Regions from '../components/Regions/Regions';
import { regionsData } from '../modelData/regions';

const container = document.getElementById('root');
const root = createRoot(container);
root.render(<Regions model={regionsData} />);