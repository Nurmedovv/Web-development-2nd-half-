const path = require('path');

module.exports = {
    mode: 'development',
    entry: {
        p1: './src/p1.jsx',
        p2: './src/p2.jsx',
        p3: './src/p3.jsx',
        p4: './src/p4.jsx'
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: '[name].bundle.js'
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env', '@babel/preset-react']
                    }
                }
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx']
    },
    devtool: 'source-map'
};