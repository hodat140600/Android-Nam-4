
SELECT *, COUNT(MANV) FROM CAPPHAT GROUP BY MANV

SELECT * FROM CAPPHAT ORDER BY MANV

SELECT * FROM NHANVIEN



SELECT DISTINCT *, SUM(SOLUONG) AS SOLUONGMUON FROM 
( SELECT * FROM VANPHONGPHAM ) AS L
JOIN
-- NÀY LÀ TÌM NHỮNG NHÂN VIÊN CÓ MẶT TRONG CẤP PHÁT ( KÈM THEO MAPB )
 (	SELECT CP.MAVPP, CP.SOLUONG, CP.MANV ,NV.HOTEN,NV.MAPB FROM CAPPHAT AS CP JOIN NHANVIEN AS NV ON CP.MANV = NV.MANV ) AS R
ON L.MAVPP = R.MAVPP
GROUP BY R.MANV
-- 