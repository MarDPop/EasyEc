sum = 0;
max = 0;
dx = 0.01;
for x = -1:dx:1
    xx = x*x;
    f = ((0.0776509570923569*xx - 0.287434475393028)*xx + 0.995181681698119)*x;
    d = abs(atan(x) - f);
    if (d > max)
        max = d;
    end
    sum = sum + d*dx;
end

sum
max