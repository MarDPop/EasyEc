clear
clc

angles = -pi/2:pi/64:pi/2;

i = 1;
for angle = angles
    a = cos(angle) * 6378137.0;
    b = sin(angle) * 6356752.3142;
    a = a*a;
    b = b*b;
    c(i) =  sqrt( (a*6378137.0^2 + b*6356752.3142^2)/(a+b));
    a = cos(angle) / 6378137.0;
    b = sin(angle) / 6356752.3142;
    d(i) = c(i) - 1 / sqrt(a * a + b * b);
    h = sin(angle);
    h = h*h;
    e(i) = c(i) - 6378174/sqrt(1+h*0.006739501254387);
    i = i + 1;
end
figure
hold on
plot(angles,d,'b')
plot(angles,e,'k')

angles = -pi/2+pi/128:pi/64:pi/2-pi/128;
i = 1;
for latitude = angles
    A = 6371000 * latitude;
    B = 6367447.280965017 * latitude - 16037.66164350688 * sin(2 * latitude) + 16.830635231967932 * sin(4 * latitude) - 0.021963382146682 * sin(6 * latitude);
    h(i) = A-B;
    i = i + 1;
end
figure
plot(angles,h);

i = 0;
for longitude = -pi:pi/10:pi
    i = i + 1;
    j = 1;
    for latitude = angles
        A = 6371000 * cos(latitude) * longitude;
        B = (6383485.515566318 * cos(latitude) - 5357.155384473197 * cos(3 * latitude) + 6.760901982543714 * cos(5 * latitude)) * longitude;
        u(i,j) = A-B;
        j = j + 1;
    end
end
figure 
hold on
for k = 1:i
    plot(angles,u(k,:))
end

% figure
% hold on
% plot(angles,A,'b')
% plot(angles,B,'k')
% 
% r = [ -0.775801556315396	-0.5411188996791423	-0.3245339452640414	];
% e = [ 0.572083460485017 -0.820195412227773 0.0	];
% n = [ -0.2661812530177462	 -0.1856605024515079 0.9458740499513433	];
% 
% figure
% hold on
% plot3([0 r(1)], [0 r(2)], [0 r(3)],'r');
% plot3([0 e(1)], [0 e(2)], [0 e(3)],'b');
% plot3([0 n(1)], [0 n(2)], [0 n(3)],':k');
% axis equal