package com.thizthizzydizzy.treefeller.menu.modify;
import com.thizthizzydizzy.simplegui.Button;
import com.thizthizzydizzy.simplegui.ItemBuilder;
import com.thizthizzydizzy.simplegui.Label;
import com.thizthizzydizzy.simplegui.Menu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
public class MenuModifyMaterialSet extends Menu{//TODO make this multi-page allowing more to be visible
    private HashSet<Material> value;
    private final boolean allowNull;
    private final String filterName;
    private final Function<Material, Boolean> filter;
    private final Consumer<HashSet<Material>> setFunc;
    public MenuModifyMaterialSet(Menu parent, Plugin plugin, Player player, String name, boolean allowNull, String filterName, HashSet<Material> defaultValue, Function<Material, Boolean> filter, Consumer<HashSet<Material>> setFunc){
        super(parent, plugin, player, "Modify Material Set ("+name+")", 54);
        this.value = (HashSet<Material>)defaultValue.clone();
        this.allowNull = allowNull;
        this.filterName = filterName;
        refresh();
        this.filter = filter;
        this.setFunc = setFunc;
    }
    public void refresh(){
        components.clear();
        add(new Button(0, new ItemBuilder(Material.PAPER).setDisplayName(value==null?"NULL":(value.size()+" "+filterName+"s")).addLore("Click a "+filterName+" in your inventory to add it to the list.").addLore("Right click any "+filterName+" in the list to remove it.").addLore(allowNull?"Shift-right click this item to set to NULL":("Shift-right click this item to clear all "+filterName+"s")), (click) -> {
            if(click==ClickType.SHIFT_RIGHT){
                if(allowNull)value = null;
                else{
                    if(value==null)value = new HashSet<>();
                    value.clear();
                }
                setFunc.accept(value);
                refresh();
            }
        }));
        if(value!=null){
            ArrayList<Material> lst = new ArrayList<>(value);
            Collections.sort(lst);
            for(int i = 0; i<Math.min(51,lst.size()); i++){
                int idx = i;
                add(new Button(i+1, new ItemBuilder(lst.get(i)), (click) -> {
                    if(click==ClickType.RIGHT){
                        value.remove(lst.get(idx));
                        setFunc.accept(value);
                        refresh();
                    }
                }));
            }
            if(lst.size()>=52){
                ItemBuilder b = new ItemBuilder(Material.PAPER);
                b.setDisplayName(lst.size()-51+" More...");
                for(int i = 52; i<lst.size(); i++){
                    b.addLore(lst.get(i).toString());
                }
                add(new Label(52, b));
            }
        }
        updateInventory();
    }
    @Override
    public void onInventoryClick(int slot, ClickType click){
        if(click!=ClickType.LEFT)return;
        ItemStack stack = player.getInventory().getItem(slot);
        if(stack==null)return;
        if(filter.apply(stack.getType())){
            if(value==null)value = new HashSet<>();
            value.add(stack.getType());
            setFunc.accept(value);
            refresh();
        }
    }
}