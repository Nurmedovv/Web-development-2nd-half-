import React from 'react';
import { createRoot } from 'react-dom/client';
import Example from '../components/Example/Example';
import { exampleData } from '../modelData/example';

const container = document.getElementById('root');
const root = createRoot(container);
root.render(<Example model={exampleData} />);