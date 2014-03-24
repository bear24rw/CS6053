python client.py --ident
python client.py --ident
python client.py --ident

while true
do
    python client.py --ident mtest16 --transfer mtest16 100 mtest17 && \
    echo "============================================================="
    sleep 5 && \
    python client.py --ident mtest17 --transfer mtest17 100 mtest18 && \
    echo "============================================================="
    sleep 5 \
    python client.py --ident mtest18 --transfer mtest18 100 mtest16 && \
    echo "============================================================="
    sleep 5

    #python client.py --ident EDSNOWDEN --transfer EDSNOWDEN 100 CORNHOLIO && \
    #echo "============================================================="
    #sleep 5 && \
    #python client.py --ident CORNHOLIO --transfer CORNHOLIO 100 GROUND_WATER && \
    #echo "============================================================="
    #sleep 5 \
    #python client.py --ident GROUND_WATER --transfer GROUND_WATER 100 EDSNOWDEN && \
    #echo "============================================================="
    #sleep 5
done
