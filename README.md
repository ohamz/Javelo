# JaVelo - Bicycle Route Planner

## Overview
JaVelo is a **bicycle route planner** specifically designed for Switzerland. It offers a user experience similar to popular online planners like Google Maps but is implemented as a **Java desktop application** that runs entirely on the user's computer without requiring an internet connection or server-side processing.

## Features
1. **Interactive Map Interface**  
   - The upper part of the interface displays the route map, while the lower part shows the elevation profile.  
   - Users can **move, zoom in, and zoom out** on the map using the mouse.

2. **Route Planning**  
   - Plan routes by placing at least two waypoints: **start** and **destination**.  
   - As soon as two points are set, JaVelo calculates the optimal cycling route considering:  
     - **Road Type**: Prefers minor roads, bike lanes, and cyclist-friendly paths.  
     - **Terrain Relief**: Avoids steep inclines to provide a more comfortable cycling experience.  

3. **Elevation Profile and Statistics**  
   - Displays the elevation profile of the calculated route.  
   - Provides detailed statistics, including:  
     - **Total distance**  
     - **Positive and negative elevation changes**  
   - When the mouse hovers over a point on the elevation profile, the corresponding location on the map is highlighted, and vice versa.

4. **Route Modification**  
   - Easily modify existing routes by:  
     - **Adding, deleting, or moving** waypoints.  
   - Any change automatically triggers a recalculation of the optimal route and its elevation profile.

## Coverage and Data Source
- JaVelo is **limited to Switzerland** due to the availability of high-precision digital elevation models.  
- It utilizes **SwissALTI3D**, an accurate elevation model provided by the **Federal Office of Topography (swisstopo)**. This dataset is publicly accessible and offers the necessary precision for reliable route planning.

## Getting Started
1. **Clone the Repository**
 
   ```bash
   git clone https://github.com/YourUsername/javelo.git
