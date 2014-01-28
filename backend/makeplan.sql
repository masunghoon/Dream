insert into plan (date, user_id, bucket_id, doneYN, lastModified)
select date_format(now(),'%Y%m%d'), user_id, id, 0, now()
from Bucket
where deadline > sysdate()
and substr(`rptCndt`,weekday(sysdate())+1,1) = 1
and rptType is not null
and rptType <> ''

union

select date_format(now(),'%Y%m%d'), b.user_id, b.id, ifnull(p.doneYN,0), now()
from Bucket b left join Plan p on b.id = p.bucket_id
where ifnull(p.doneYN,0) <> 1
and b.deadline between ADDDATE(LAST_DAY(SUBDATE(NOW(), INTERVAL 1 MONTH)), 1) and ADDDATE(LAST_DAY(NOW()),1)

union

select date_format(now(),'%Y%m%d'), b.user_id, b.id, ifnull(p.doneYN,0), now()
from Bucket b left join Plan p on b.id = p.bucket_id
where ifnull(p.doneYN,0) <> 1
and b.scope = 'YEARLY'
and b.range = date_format(now(),'%Y')
and date_format(now(),'%m') = '12';