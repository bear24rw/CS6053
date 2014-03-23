#rsync -avz --stats --progress -e "ssh -i id_rsa" ./ stackattack@helios.ececs.uc.edu:~/netsec/
scp -i id_rsa ./* stackattack@helios.ececs.uc.edu:~/netsec/
