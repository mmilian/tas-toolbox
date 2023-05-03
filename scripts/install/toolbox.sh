#!/usr/bin/env bash

set -euo pipefail

export_path_cmd() {
    echo
    echo '  export PATH="${PATH}:'"$1"'"'
}

append_to_file() {
    local -r path="$1"
    local -r text="$2"
    if [ -f "$path" ]; then
        echo "$text" >>"$path"
    fi
}

local_bin_dir="~/.local/bin"
# just in case
mkdir -p $local_bin_dir

TAS_TOOLBOX_HOME="$HOME/.tas-toolbox"
tas_toolbox_repo='git@github.com:mmilian/tas-toolbox.git'

if [ -f $TAS_TOOLBOX_HOME ]; then
    cd $TAS_TOOLBOX_HOME
    git pull
else
    mkdir -p $TAS_TOOLBOX_HOME
    git clone $TAS_TOOLBOX_REPO $TAS_TOOLBOX_HOME
fi

cd $TAS_TOOLBOX_HOME
cd scripts/install
chmod +x *.sh
./bootstrap_toolbox.sh

if echo "$PATH" | grep -q "$local_bin_dir"; then
    :
else
    local -r cmd="$(export_path_cmd "$local_bin_dir")"
    append_to_file "${HOME}/.bashrc" "$cmd"
    append_to_file "${ZDOTDIR:-"$HOME"}/.zshrc" "$cmd"
fi

echo "Successfully installed $(toolbox) in $TAS_TOOLBOX_HOME."
echo "To use toolbox, restart your shell or reload your .bashrc-like config file"
echo "Check https://github.com/denisidoro/navi for more info"

export PATH="${PATH}:${local_bin_dir}"

return 0
