clear 
clc

M = csvread('World.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
value = M(:,3);

[xq,yq] = meshgrid(linspace(-90,-70,1001),linspace(10,30,1001));
z3 = griddata(long,lat,value,xq,yq,'natural');

figure
hold on
min = -16;
max = -9;
exp = linspace(min,max,21);
[c, t] = contourf(xq,yq,log10(z3+1e-50),exp, 'EdgeColor','none');
colorbar

M = csvread('traj.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
plot(long,lat,'k');

figure
hold on

M = csvread('centroids.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
sz = M(:,3);
sz2 = sqrt(M(:,4)+M(:,5))./1e4;
scatter(long,lat,sz,'r');
scatter(long,lat,sz2,'b');

M = csvread('traj.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
h = M(:,3);
plot(long,lat,'k');

% M = csvread('data.csv');
% long = M(1:5:end,1).*180/pi;
% lat = M(1:5:end,2).*180/pi;
% scatter(long,lat,'y');

% figure
% hold on;
% ax = worldmap('World');
% land = shaperead('landareas', 'UseGeoCoords', true);
% [c,t] = contourm(yq,xq,log10(z3+1e-50),exp);
% clegendm(c,t,-1);
% geoshow(ax, land, 'FaceColor', [0.5 0.7 0.5]);

