package buildutilities

interface BuildFactory {
  def checkoutScm()
  def clean()
  def compile()
  def test()
  def staticAnalysis()
  def publish()
}
