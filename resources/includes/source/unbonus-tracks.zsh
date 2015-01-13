#!/usr/bin/zsh

IFS=$'\n';

suffix=" (Bonus Tracks Version)";
root="/home/emallson/Music/";

for songpath in $(mpc search album "$suffix"); do
    echo $songpath;
    tag=`metaflac --show-tag=ALBUM "$root$songpath"`;
    metaflac --remove-tag=ALBUM "$root$songpath";
    metaflac --set-tag="ALBUM=${${${(C)tag#ALBUM=}%$suffix}/The /the }" \
        "$root$songpath";
done
