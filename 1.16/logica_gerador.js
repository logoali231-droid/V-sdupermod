onEvent('recipes', event => {
  event.shaped('1x kubejs:resource_generator', [
    ' I ', // Topo: Vazio, Bloco de Ferro, Vazio
    'IDI', // Meio: Bloco de Ferro, Diamante, Bloco de Ferro
    ' H '  // Fundo: Vazio, Funil, Vazio
  ], {
    I: 'minecraft:iron_block',
    D: 'minecraft:iron_ingot',
    H: 'minecraft:hopper'
  })
})

// 2. A Lógica de Bater (Clicar) no bloco para injetar 64 itens
onEvent('block.right_click', event => {
  // Verifica se clicou no teu gerador customizado
  if (event.block.id == 'kubejs:resource_generator') {
    let downBlock = event.block.down
    
    // Verifica se há um baú/drawer logo abaixo
    if (downBlock.hasInventory()) {
      let itemHand = event.player.mainHandItem
      
      // Se tiveres um item na mão, insere 64 cópias dele no inventário
      if (!itemHand.isEmpty()) {
        downBlock.inventory.insertItem(itemHand.id, 64, false)
        event.player.tell('Duplicou 64x ' + itemHand.name + ' com sucesso!')
        event.cancel() // Cancela a animação padrão da mão
      } else {
        event.player.tell('Tens de segurar o item que queres duplicar!')
      }
    } else {
      event.player.tell('Coloca um baú ou drawer debaixo do gerador!')
    }
  }
})
