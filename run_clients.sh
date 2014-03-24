POINTS=200
DELAY=60    # (60seconds/min * 60min/hour)/(150commands/hour / 7commands/transfer)/3 transfers

#python client.py --ident mtest16
#python client.py --ident mtest17
#python client.py --ident mtest18

python client.py --alive --ident EDSNOWDEN
python client.py --alive --ident CORNHOLIO
python client.py --alive --ident GROUND_WATER

echo "====================== STARTING =============================" && \
while true
do
    #python client.py --ident mtest16 --transfer mtest16 100 mtest17      && \
    #echo "=============================================================" && \
    #sleep 10                                                             && \
    #python client.py --ident mtest17 --transfer mtest17 100 mtest18      && \
    #echo "=============================================================" && \
    #sleep 10                                                             && \
    #python client.py --ident mtest18 --transfer mtest18 100 mtest16      && \
    #echo "=============================================================" && \
    #sleep 10

    python client.py --ident EDSNOWDEN --transfer EDSNOWDEN $POINTS CORNHOLIO       && \
    echo "============================================================="        && \
    sleep $DELAY                                                                    && \
    python client.py --ident CORNHOLIO --transfer CORNHOLIO $POINTS GROUND_WATER    && \
    echo "============================================================="        && \
    sleep $DELAY                                                                    && \
    python client.py --ident GROUND_WATER --transfer GROUND_WATER $POINTS EDSNOWDEN && \
    echo "============================================================="        && \
    sleep $DELAY
done
