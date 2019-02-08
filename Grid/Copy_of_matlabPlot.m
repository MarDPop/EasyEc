clear 
clc

figure
hold on
M = csvread('data.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
scatter(long,lat,'g');

M = csvread('traj.csv');
long = M(:,1).*180/pi;
lat = M(:,2).*180/pi;
plot(long,lat,'k');

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

figure
hold on
for i = 1:20:600
    M = csvread(['frag' num2str(i)]);
    [m, ~] = size(M);
    l = 1:10:m;
    N = zeros(length(l),5);
    k = 1;
    for j = l
        d = -M(j,4)*7.29211505392569E-5;
        N(k,1) = M(j,1)*cos(d)-M(j,2)*sin(d);
        N(k,2) = M(j,1)*sin(d)+M(j,2)*cos(d);
        N(k,3) = M(j,3);
        N(k,4) = M(j,4);
        N(k,5) = sqrt(N(k,1)^2+N(k,2)^2+N(k,3)^2)-6371000;
        k = k + 1;
    end
    plot3(N(:,1),N(:,2),N(:,3),':r');
% plot(N(:,4),N(:,5),':r');
end
% [x, y, z] = sphere(50);
% surf(x.*6371000,y.*6371000,z.*6371000, 'FaceColor', [0.1 0.1 1], 'FaceAlpha', 0.2)