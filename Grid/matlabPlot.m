clear 
clc

M = csvread('World.csv');
[m, n] = size(M);
long = zeros(m,1);
lat = M(:,2)./6371000;
for i = 1:m
    for iter = 1:8
        oldLat = lat(i);
        lat(i) = (M(i,2) + 16037.66164350688 * sin(2 * oldLat) - 16.830635231967932 * sin(4 * oldLat) + 0.021963382146682 * sin(6 * oldLat))/6367447.280965017 ; 
    end
    long(i) = M(i,1) / (6383485.515566318 * cos(lat(i)) - 5357.155384473197 * cos(3 * lat(i)) + 6.760901982543714 * cos(5 * lat(i)));
end

lat = lat.*180/pi;
long = long.*180/pi;
value = M(:,3);

% [xq,yq] = meshgrid(linspace(-180,max(long),1000),linspace(min(lat),max(lat),1000));
[xq,yq] = meshgrid(linspace(-135,-75,751),linspace(-15,35,751));
z3 = griddata(long,lat,value,xq,yq,'natural');

% M = csvread('initial.csv');
% long = M(:,1);
% lat = M(:,2);
% sz = M(:,3);
% sz2 = (M(:,4)+M(:,5))./1e8;

figure
hold on
% contourf(xq,yq,z3,[5e-14 1e-13 2e-13 4e-13 8e-13 1.6e-12 3e-12 6e-12 1e-11 2e-11 4e-11 8e-11 1.6e-10 3.2e-10 6.4e-10 1.28e-9 2.56e-9 ])
min = -14;
max = -10;
exp = linspace(min,max,21);
% levels = 10.^exp;
% l = 1+63*(exp-min)/(max-min);
% hC = colorbar;
% set(hC,'Ytick',l,'YTicklabel',levels);
[c, t] = contourf(xq,yq,log10(z3+1e-50),exp, 'EdgeColor','none');
colorbar

M = csvread('traj.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
plot(long,lat,'k');
% plot([long(2) long(1)], [lat(2) lat(1)],'r');
% plot([long(1) long(3)], [lat(1) lat(3)],'r');
% plot([long(3) long(4)], [lat(3) lat(4)],'r');
% plot([long(4) long(2)], [lat(4) lat(2)],'r');

M = csvread('centroids.csv');
[m, n] = size(M);
long = zeros(m,1);
lat = M(:,2)./6371000;
for i = 1:m
    c = cos(lat(i));
    sl = 1/sqrt((c/6378137)^2+(sin(lat(i))/6356752.3)^2);
    long(i) = M(i,1)/(c*sl);
end
lat = lat.*180/pi;
long = long.*180/pi;
sz = M(:,3);
scatter(long,lat,sz,'b');

% scatter(long,lat);
% scatter(long,lat,sz);
% scatter(long,lat,sz2,'r');
figure
hold on
% Impacts = csvread('data.csv');
% [m, n] = size(Impacts);
% longImpacts = zeros(m,1);
% latImpacts = Impacts(:,2)./6371000;
% for i = 1:m
%     c = cos(latImpacts(i));
%     sl = 1/sqrt((c/6378137)^2+(sin(latImpacts(i))/6356752.3)^2);
%     longImpacts(i) = Impacts(i,1)/(c*sl);
% end
% latImpacts = latImpacts.*180/pi;
% longImpacts = longImpacts.*180/pi;
% scatter(longImpacts,latImpacts,'y');

M = csvread('centroids.csv');
[m, n] = size(M);
long = zeros(m,1);
lat = M(:,2)./6371000;
for i = 1:m
    c = cos(lat(i));
    sl = 1/sqrt((c/6378137)^2+(sin(lat(i))/6356752.3)^2);
    long(i) = M(i,1)/(c*sl);
end
lat = lat.*180/pi;
long = long.*180/pi;
sz = M(:,3);
sz2 = sqrt(M(:,4)+M(:,5))./1e4;
scatter(long,lat,sz,'r');
scatter(long,lat,sz2,'b');

M = csvread('traj.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
h = M(:,3);
plot(long,lat,'k');

% figure
% hold on;
% ax = worldmap('World');
% land = shaperead('landareas', 'UseGeoCoords', true);
% [c,t] = contourm(yq,xq,log10(z3+1e-50),exp);
% clegendm(c,t,-1);
% geoshow(ax, land, 'FaceColor', [0.5 0.7 0.5]);


