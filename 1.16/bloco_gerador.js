onEvent('block.registry', event => {
  event.create('resource_generator')
    .displayName('Resource Generator')
    .material('iron')
    .hardness(3.0)
    .textureAll('minecraft:block/iron_block')
    .color(0, 0x00FFBB) 
})
