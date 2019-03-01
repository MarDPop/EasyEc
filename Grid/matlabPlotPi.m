%% Plots Pi Contour
% Author: Marius Popescu
% Date: 2/1/2019
% Version: 1.0

clear
clc

M = csvread('World.csv');
long = M(:,1).*57.295779513082323;
lat = M(:,2).*57.295779513082323;

[xq,yq] = meshgrid(linspace(-140,-30,2001),linspace(-10,45,1001));
z3 = griddata(long,lat,M(:,3),xq,yq,'natural');
% 
% figure
% hold on
min = -15;
max = -7;
exp = linspace(min,max,25);
% [c, t] = contourf(xq,yq,log10(z3),exp, 'EdgeColor','none');
% colorbar


figure
hold on
% 
M = csvread('centroids.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
sz = M(:,3)/2;
sz2 = sqrt(M(:,4)+M(:,5))./5e3;
scatter(long,lat,sz,'r');
scatter(long,lat,sz2,'c');

M = csvread('traj.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
h = M(:,3);
plot(long,lat,':k','LineWidth',2);

figure
M = csvread('data.csv');
xs = M(1:end,1);
ys = M(1:end,2);
scatter(xs,ys,'x');

% 
figure
hold on
M = csvread('impacts.csv');
X = M(1:end,1);
Y = M(1:end,2);
Z = M(1:end,3);
scatter3(X,Y,Z);

% [x,y,z] = sphere(30);
% surf(x*6378137,y*6378137,z*6356752.314);

figure
hold on;
ax = worldmap('World');
land = shaperead('landareas', 'UseGeoCoords', true);
[c,t] = contourm(yq,xq,log10(z3),exp);
clegendm(c,t,-1);
geoshow(ax, land, 'FaceColor', [0.5 0.7 0.5]);


